use diesel::pg::Pg;
use diesel::prelude::*;
use diesel::query_builder::*;
use diesel::sql_types::BigInt;
use diesel_async::methods::LoadQuery;
use diesel_async::AsyncPgConnection;
use serde::Deserialize;

#[derive(Debug, Deserialize)]
pub struct PaginationParams {
    #[serde(default = "default_page", deserialize_with = "validate_page")]
    pub page: i64,
    #[serde(default = "default_page_size", deserialize_with = "validate_page_size")]
    pub page_size: i64,
}

const MIN_PAGE: i64 = 1;
const DEFAULT_PAGE: i64 = 1;
const MIN_PAGE_SIZE: i64 = 1;
const MAX_PAGE_SIZE: i64 = 100;
const DEFAULT_PAGE_SIZE: i64 = 20;

fn default_page() -> i64 {
    DEFAULT_PAGE
}

fn default_page_size() -> i64 {
    DEFAULT_PAGE_SIZE
}

fn validate_page<'de, D>(deserializer: D) -> Result<i64, D::Error>
where
    D: serde::Deserializer<'de>,
{
    let page = i64::deserialize(deserializer)?;
    if page < MIN_PAGE {
        Ok(MIN_PAGE)
    } else {
        Ok(page)
    }
}

fn validate_page_size<'de, D>(deserializer: D) -> Result<i64, D::Error>
where
    D: serde::Deserializer<'de>,
{
    let page_size = i64::deserialize(deserializer)?;
    if page_size < MIN_PAGE_SIZE {
        Ok(MIN_PAGE_SIZE)
    } else if page_size > MAX_PAGE_SIZE {
        Ok(MAX_PAGE_SIZE)
    } else {
        Ok(page_size)
    }
}

impl Default for PaginationParams {
    fn default() -> Self {
        PaginationParams { page: DEFAULT_PAGE, page_size: DEFAULT_PAGE_SIZE }
    }
}

pub trait Paginate: Sized {
    fn paginate(self, page: i64) -> Paginated<Self>;
}

impl<T> Paginate for T {
    fn paginate(self, page: i64) -> Paginated<Self> {
        Paginated {
            query: self,
            page_size: DEFAULT_PAGE_SIZE,
            page,
            offset: (page - 1) * DEFAULT_PAGE_SIZE,
        }
    }
}

#[derive(Debug, Clone, Copy, QueryId)]
pub struct Paginated<T> {
    query: T,
    page: i64,
    page_size: i64,
    offset: i64,
}

impl<T> Paginated<T> {
    pub fn page_size(self, page_size: i64) -> Self {
        Paginated { page_size, offset: (self.page - 1) * page_size, ..self }
    }

    pub async fn load_and_count_pages<'a, U>(
        self,
        conn: &mut AsyncPgConnection,
    ) -> QueryResult<(Vec<U>, i64)>
    where
        T: 'a,
        U: Send + 'a,
        Self: LoadQuery<'a, AsyncPgConnection, (U, i64)>,
    {
        let page_size = self.page_size;
        let results = diesel_async::RunQueryDsl::load::<(U, i64)>(self, conn).await?;
        let total = results.first().map(|x| x.1).unwrap_or(0);
        let records = results.into_iter().map(|x| x.0).collect();
        let total_pages = (total as f64 / page_size as f64).ceil() as i64;
        Ok((records, total_pages))
    }
}

impl<T: Query> Query for Paginated<T> {
    type SqlType = (T::SqlType, BigInt);
}

impl<T> RunQueryDsl<AsyncPgConnection> for Paginated<T> {}

impl<T> QueryFragment<Pg> for Paginated<T>
where
    T: QueryFragment<Pg>,
{
    fn walk_ast<'b>(&'b self, mut out: AstPass<'_, 'b, Pg>) -> QueryResult<()> {
        out.push_sql("SELECT *, COUNT(*) OVER () FROM (");
        self.query.walk_ast(out.reborrow())?;
        out.push_sql(") t LIMIT ");
        out.push_bind_param::<BigInt, _>(&self.page_size)?;
        out.push_sql(" OFFSET ");
        out.push_bind_param::<BigInt, _>(&self.offset)?;
        Ok(())
    }
}
