mod api;
mod db;
mod models;
mod pagination;
mod schema;

use actix_cors::Cors;
use actix_web::{App, HttpResponse, HttpServer, Responder, get, web};
use api::auth::config_auth;
use api::messages::config_messages;
use api::ratings::config_ratings;
use api::reports::config_reports;
use api::train_lines::config_train_lines;
use api::users::config_users;
use db::establish_connection;
use serde::Serialize;

#[derive(Serialize)]
pub struct Response {
    pub message: String,
}

#[get("/health")]
async fn healthcheck() -> impl Responder {
    let pool = establish_connection();

    match pool.get().await {
        Ok(_) => HttpResponse::Ok().json(Response { message: "Healthy".to_string() }),
        Err(_) => {
            HttpResponse::InternalServerError().json(Response { message: "Unhealthy".to_string() })
        }
    }
}

async fn not_found() -> impl Responder {
    HttpResponse::NotFound().json(Response { message: "Not Found".to_string() })
}

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    let db_pool = establish_connection();
    let port = std::env::var("PORT").unwrap_or_else(|_| "8000".to_string());
    println!("Server running at http://localhost:{}", port);
    HttpServer::new(move || {
        let cors = Cors::default()
            .allowed_methods(vec!["GET", "POST", "PATCH", "PUT", "DELETE"])
            .allow_any_header()
            .allow_any_origin()
            .max_age(3600);
        App::new()
            .app_data(web::Data::new(db_pool.clone()))
            .service(healthcheck)
            .configure(config_users)
            .configure(config_auth)
            .configure(config_ratings)
            .configure(config_reports)
            .configure(config_train_lines)
            .configure(config_messages)
            .default_service(web::route().to(not_found))
            .wrap(cors)
            .wrap(actix_web::middleware::Logger::default())
    })
    .bind(("0.0.0.0", port.parse().expect("Can't parse PORT env var")))?
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
