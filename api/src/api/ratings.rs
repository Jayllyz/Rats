use crate::api::utils;
use crate::models::ratings_models::{CreateRating, RatingResponse};
use crate::schema::ratings;
use crate::{db::DbPool, models::ratings_models::RatingRequest};
use actix_web::{get, post, web, HttpRequest, HttpResponse, Result};
use diesel::prelude::*;
use diesel_async::RunQueryDsl;

#[get("")]
async fn get_ratings(pool: web::Data<DbPool>) -> Result<HttpResponse> {
    let mut conn = pool.get().await.expect("Couldn't get db connection from pool");

    let ratings = ratings::table
        .load::<RatingResponse>(&mut conn)
        .await
        .map_err(|_| actix_web::error::ErrorInternalServerError("Error querying ratings"))?;

    Ok(HttpResponse::Ok().json(ratings))
}

#[get("/{id}")]
async fn get_rating(pool: web::Data<DbPool>, id_rating: web::Path<i32>) -> Result<HttpResponse> {
    let mut conn = pool.get().await.expect("Couldn't get db connection from pool");

    let result =
        ratings::table.filter(ratings::id.eq(*id_rating)).first::<RatingResponse>(&mut conn).await;

    match result {
        Ok(rating) => Ok(HttpResponse::Ok().json(rating)),
        Err(diesel::result::Error::NotFound) => {
            Ok(HttpResponse::NotFound().body("Rating not found"))
        }
        Err(_) => Ok(HttpResponse::InternalServerError().finish()),
    }
}

#[get("/sender/{id}")]
async fn get_ratings_by_sender(
    pool: web::Data<DbPool>,
    id_sender: web::Path<i32>,
) -> Result<HttpResponse> {
    let mut conn = pool.get().await.expect("Couldn't get db connection from pool");

    match utils::user_exists(&mut conn, *id_sender).await {
        Ok(_) => (),
        Err(e) => return Err(e),
    }

    let ratings = ratings::table
        .filter(ratings::id_sender.eq(*id_sender))
        .load::<RatingResponse>(&mut conn)
        .await
        .map_err(|_| actix_web::error::ErrorInternalServerError("Error querying ratings"))?;

    Ok(HttpResponse::Ok().json(ratings))
}

#[get("/receiver/{id}")]
async fn get_ratings_by_receiver(
    pool: web::Data<DbPool>,
    id_receiver: web::Path<i32>,
) -> Result<HttpResponse> {
    let mut conn = pool.get().await.expect("Couldn't get db connection from pool");

    match utils::user_exists(&mut conn, *id_receiver).await {
        Ok(_) => (),
        Err(e) => return Err(e),
    }

    let ratings = ratings::table
        .filter(ratings::id_receiver.eq(*id_receiver))
        .load::<RatingResponse>(&mut conn)
        .await
        .map_err(|_| actix_web::error::ErrorInternalServerError("Error querying ratings"))?;

    Ok(HttpResponse::Ok().json(ratings))
}

#[post("/{id}")]
async fn create_rating(
    pool: web::Data<DbPool>,
    id_receiver: web::Path<i32>,
    rating: web::Json<RatingRequest>,
    req: HttpRequest,
) -> Result<HttpResponse> {
    let id_user = match utils::validate_token(&req) {
        Ok(claims) => claims.sub,
        Err(err) => return Err(actix_web::error::ErrorUnauthorized(err)),
    };

    let new_rating = CreateRating {
        id_sender: id_user,
        id_receiver: *id_receiver,
        stars: rating.stars,
        comment: rating.comment.clone(),
    };

    let mut conn = pool.get().await.expect("Couldn't get db connection from pool");

    match diesel::insert_into(ratings::table)
        .values(&new_rating)
        .get_result::<RatingResponse>(&mut conn)
        .await
    {
        Ok(rating) => Ok(HttpResponse::Created().json(rating)),
        Err(e) => match e {
            diesel::result::Error::DatabaseError(
                diesel::result::DatabaseErrorKind::ForeignKeyViolation,
                _,
            ) => Err(actix_web::error::ErrorBadRequest("Invalid sender or receiver")),
            _ => {
                Err(actix_web::error::ErrorInternalServerError(format!("Database error: {:?}", e)))
            }
        },
    }
}

pub fn config_ratings(cfg: &mut web::ServiceConfig) {
    cfg.service(
        web::scope("/ratings")
            .service(get_ratings)
            .service(get_rating)
            .service(get_ratings_by_sender)
            .service(get_ratings_by_receiver)
            .service(create_rating),
    );
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::{api::auth::config_auth, api::utils::user_id_by_email, db};
    use actix_web::{http, test, App};
    use db::establish_connection;
    use diesel_async::RunQueryDsl;

    async fn create_test_user(email: &str) -> Result<i32, Box<dyn std::error::Error>> {
        let pool = establish_connection();
        let app = test::init_service(
            App::new().configure(config_auth).app_data(web::Data::new(pool.clone())),
        )
        .await;

        let req = test::TestRequest::post()
            .uri("/auth/signup")
            .set_json(&crate::models::users_models::CreateUser {
                name: email.to_string(),
                email: email.to_string(),
                password: "testpassword".to_string(),
            })
            .to_request();

        let resp = test::call_service(&app, req).await;

        if resp.status().is_success() {
            let mut conn = pool.get().await?;
            user_id_by_email(&mut conn, email).await.map_err(|e| e.into())
        } else {
            Err(format!("Failed to create user. Status: {}", resp.status()).into())
        }
    }

    async fn delete_test_user(user_id: i32) {
        let pool = establish_connection();
        let mut conn = pool.get().await.expect("Couldn't get db connection from pool");
        diesel::delete(crate::schema::users::table.filter(crate::schema::users::id.eq(user_id)))
            .execute(&mut conn)
            .await
            .expect("Error deleting user");
    }

    #[actix_web::test]
    async fn test_get_all_ratings() {
        let pool = establish_connection();
        let app = test::init_service(
            App::new().configure(config_ratings).app_data(web::Data::new(pool.clone())),
        )
        .await;

        let req = test::TestRequest::get().uri("/ratings").to_request();
        let resp = test::call_service(&app, req).await;
        assert_eq!(resp.status(), http::StatusCode::OK);
    }

    #[actix_web::test]
    async fn test_get_rating_not_found() {
        let pool = establish_connection();
        let app = test::init_service(
            App::new().configure(config_ratings).app_data(web::Data::new(pool.clone())),
        )
        .await;

        let req = test::TestRequest::get().uri("/ratings/9999").to_request();
        let resp = test::call_service(&app, req).await;
        assert_eq!(resp.status(), http::StatusCode::NOT_FOUND);
    }

    #[actix_web::test]
    async fn test_get_ratings_by_receiver() {
        let pool = establish_connection();
        let app = test::init_service(
            App::new()
                .configure(config_ratings)
                .configure(config_auth)
                .app_data(web::Data::new(pool.clone())),
        )
        .await;

        let user_id =
            create_test_user("test_receiver@example.com").await.expect("Error creating user");

        let req =
            test::TestRequest::get().uri(&format!("/ratings/receiver/{}", user_id)).to_request();
        let resp = test::call_service(&app, req).await;
        assert_eq!(resp.status(), http::StatusCode::OK);

        delete_test_user(user_id).await;
    }

    #[actix_web::test]
    async fn test_get_ratings_by_sender() {
        let pool = establish_connection();
        let app = test::init_service(
            App::new()
                .configure(config_ratings)
                .configure(config_auth)
                .app_data(web::Data::new(pool.clone())),
        )
        .await;

        let user_id =
            create_test_user("test_sender@example.com").await.expect("Error creating user");

        let req =
            test::TestRequest::get().uri(&format!("/ratings/sender/{}", user_id)).to_request();
        let resp = test::call_service(&app, req).await;
        assert_eq!(resp.status(), http::StatusCode::OK);

        delete_test_user(user_id).await;
    }

    #[actix_web::test]
    async fn test_create_rating() {
        let pool = establish_connection();
        let app = test::init_service(
            App::new()
                .configure(config_ratings)
                .configure(config_auth)
                .app_data(web::Data::new(pool.clone())),
        )
        .await;

        let sender_email = "test_sender_create@example.com";
        let sender_id = create_test_user(sender_email).await.expect("Error creating sender");
        let receiver_id = create_test_user("test_receiver_create@example.com")
            .await
            .expect("Error creating receiver");

        let login_req = test::TestRequest::post()
            .uri("/auth/login")
            .set_json(&crate::models::users_models::LoginRequest {
                email: sender_email.to_string(),
                password: "testpassword".to_string(),
            })
            .to_request();
        let login_resp = test::call_service(&app, login_req).await;
        assert_eq!(login_resp.status(), http::StatusCode::OK);

        let login_body = test::read_body(login_resp).await;
        let login_json: serde_json::Value =
            serde_json::from_slice(&login_body).expect("Invalid JSON");
        let token = login_json["token"].as_str().expect("Token not found in response");

        let req = test::TestRequest::post()
            .uri(&format!("/ratings/{}", receiver_id))
            .insert_header(("Authorization", format!("Bearer {}", token)))
            .set_json(&RatingRequest { stars: 5, comment: Some("Great service!".to_string()) })
            .to_request();
        let resp = test::call_service(&app, req).await;
        assert_eq!(resp.status(), http::StatusCode::CREATED);

        delete_test_user(sender_id).await;
        delete_test_user(receiver_id).await;
    }
}
