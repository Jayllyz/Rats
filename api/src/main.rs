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

#[cfg(test)]
mod tests {
    use super::*;
    use actix_web::{http, test};

    #[actix_web::test]
    async fn test_healthcheck() {
        let app = test::init_service(App::new().service(healthcheck)).await;
        let req = test::TestRequest::get().uri("/health").to_request();
        let resp = test::call_service(&app, req).await;

        assert_eq!(resp.status(), http::StatusCode::OK);
    }

    #[actix_web::test]
    async fn test_not_found() {
        let app = test::init_service(App::new().default_service(web::route().to(not_found))).await;
        let req = test::TestRequest::get().uri("/non-existent-route").to_request();
        let resp = test::call_service(&app, req).await;

        assert_eq!(resp.status(), http::StatusCode::NOT_FOUND);
    }
}
