use diesel::{
    r2d2::{ConnectionManager, Pool},
    PgConnection,
};
use dotenvy::dotenv;
use std::env;

pub type DbPool = Pool<ConnectionManager<PgConnection>>;

pub fn establish_connection() -> DbPool {
    dotenv().ok();
    let database_url = env::var("DATABASE_URL").expect("DATABASE_URL must be set");
    let manager = ConnectionManager::<PgConnection>::new(database_url);
    Pool::builder().build(manager).expect("Failed to create db pool.")
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_database_connection() {
        let db_pool = establish_connection().get();
        assert!(db_pool.is_ok(), "Should be able to get a connection from the pool");
    }
}
