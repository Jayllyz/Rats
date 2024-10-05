mod api;
mod db;
mod models;
mod schema;

use actix_web::{web, App, HttpServer};
use api::users::{create_user, get_all_users};
use db::establish_connection;

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    let db_pool = establish_connection();
    HttpServer::new(move || {
        App::new()
            .app_data(web::Data::new(db_pool.clone()))
            .service(create_user)
            .service(get_all_users)
    })
    .bind(("127.0.0.1", 8080))?
    .run()
    .await
}
