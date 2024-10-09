use diesel_async::pooled_connection::deadpool::Pool;
use diesel_async::{pooled_connection::AsyncDieselConnectionManager, AsyncPgConnection};
use dotenvy::dotenv;
use std::env;

pub type DbPool = Pool<AsyncPgConnection>;

pub fn establish_connection() -> DbPool {
    dotenv().ok();
    let db_url = env::var("DATABASE_URL").expect("DATABASE_URL must be set");
    let config = AsyncDieselConnectionManager::<AsyncPgConnection>::new(db_url);
    Pool::builder(config).build().expect("Failed to create pool")
}

#[cfg(test)]
mod tests {
    use super::*;
    use diesel_async::RunQueryDsl;

    #[actix_web::test]
    async fn test_database_connection() {
        let pool = establish_connection();
        let mut conn = pool.get().await.expect("Failed to get connection from pool");

        // Execute a simple query to test the connection
        let result = diesel::sql_query("SELECT 1").execute(&mut conn).await;

        assert!(result.is_ok(), "Should be able to execute a query");
    }
}
