mod api;
mod db;
mod models;
mod schema;

use actix_web::{get, web, App, HttpResponse, HttpServer, Responder};
use api::auth::config_auth;
use api::users::config_users;
use db::establish_connection;
use serde::Serialize;

#[derive(Serialize)]
pub struct Response {
    pub message: String,
}

#[get("/health")]
async fn healthcheck() -> impl Responder {
    HttpResponse::Ok().json(Response { message: "OK".to_string() })
}

async fn not_found() -> impl Responder {
    HttpResponse::NotFound().json(Response { message: "Not Found".to_string() })
}

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    let db_pool = establish_connection();
    HttpServer::new(move || {
        App::new()
            .app_data(web::Data::new(db_pool.clone()))
            .service(healthcheck)
            .configure(config_users)
            .configure(config_auth)
            .default_service(web::route().to(not_found))
            .wrap(actix_web::middleware::Logger::default())
    })
    .bind(("127.0.0.1", 8080))?
    .run()
    .await
}
