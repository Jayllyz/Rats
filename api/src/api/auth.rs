use crate::api::utils;
use crate::db::DbPool;
use crate::models::users_models::{
    CreateUser, LoginRequest, LoginResponse, SignupRequest, UserResponse,
};
use crate::schema::users;
use actix_web::{post, web, HttpResponse, Result};
use diesel::prelude::*;
use diesel_async::RunQueryDsl;
use password_auth::{generate_hash, verify_password};

#[post("/signup")]
async fn signup(pool: web::Data<DbPool>, user: web::Json<SignupRequest>) -> Result<HttpResponse> {
    let password_to_hash = user.password.clone();
    let password_hash = web::block(move || generate_hash(&password_to_hash))
        .await
        .map_err(|_| actix_web::error::ErrorInternalServerError("Error hashing password"))?;

    let new_user =
        CreateUser { name: user.name.clone(), email: user.email.clone(), password: password_hash };

    let mut conn = pool.get().await.expect("Couldn't get db connection from pool");

    match diesel::insert_into(users::table)
        .values(&new_user)
        .get_result::<UserResponse>(&mut conn)
        .await
    {
        Ok(user) => Ok(HttpResponse::Created().json(user)),
        Err(e) => match e {
            diesel::result::Error::DatabaseError(
                diesel::result::DatabaseErrorKind::UniqueViolation,
                _,
            ) => Err(actix_web::error::ErrorConflict("User already exists")),
            _ => {
                Err(actix_web::error::ErrorInternalServerError(format!("Database error: {:?}", e)))
            }
        },
    }
}

#[post("/login")]
async fn login(
    pool: web::Data<DbPool>,
    credentials: web::Json<LoginRequest>,
) -> Result<HttpResponse> {
    let mut conn = pool.get().await.expect("Couldn't get db connection from pool");

    let user = users::table
        .filter(users::email.eq(&credentials.email))
        .first::<UserResponse>(&mut conn)
        .await
        .map_err(|e| match e {
            diesel::result::Error::NotFound => actix_web::error::ErrorNotFound("User not found"),
            _ => actix_web::error::ErrorInternalServerError(format!("Database error: {}", e)),
        })?;

    if verify_password(&credentials.password, &user.password).is_err() {
        return Err(actix_web::error::ErrorUnauthorized("Invalid credentials"));
    }

    let token = utils::create_token(&user.id).map_err(|e| {
        actix_web::error::ErrorInternalServerError(format!("Token creation error: {}", e))
    })?;

    diesel::update(users::table.filter(users::id.eq(user.id)))
        .set(users::token.eq(&token))
        .execute(&mut conn)
        .await
        .map_err(|_| actix_web::error::ErrorInternalServerError("Failed to update user token"))?;

    Ok(HttpResponse::Ok().json(LoginResponse { token }))
}

pub fn config_auth(cfg: &mut web::ServiceConfig) {
    cfg.service(web::scope("/auth").service(signup).service(login));
}

#[cfg(test)]
mod tests {
    use crate::{db, models};

    use super::*;
    use actix_web::{http, test, App};
    use db::establish_connection;
    use models::users_models::CreateUser;
    use std::time::{SystemTime, UNIX_EPOCH};

    fn generate_string() -> String {
        let nanos = SystemTime::now()
            .duration_since(UNIX_EPOCH)
            .expect("Time went backwards")
            .subsec_nanos();
        format!("test_{}", nanos)
    }

    #[actix_web::test]
    async fn test_create_token() {
        let token = utils::create_token(&1).unwrap();
        assert_ne!(token, "");
    }

    #[actix_web::test]
    async fn auth_workflow() {
        let pool = establish_connection();
        let app = test::init_service(
            App::new().configure(config_auth).app_data(web::Data::new(pool.clone())),
        )
        .await;

        let random_email = format!("{}@example.com", generate_string());
        let random_password = generate_string();

        let req = test::TestRequest::post()
            .uri("/auth/signup")
            .set_json(&CreateUser {
                name: "Test User".to_string(),
                email: random_email.clone(),
                password: random_password.clone(),
            })
            .to_request();

        let resp = test::call_service(&app, req).await;
        assert_eq!(resp.status(), http::StatusCode::CREATED);

        let req = test::TestRequest::post()
            .uri("/auth/login")
            .set_json(&LoginRequest {
                email: random_email.clone(),
                password: random_password.clone(),
            })
            .to_request();

        let resp = test::call_service(&app, req).await;
        assert_eq!(resp.status(), http::StatusCode::OK);
        assert_eq!(resp.headers().get("content-type").unwrap(), "application/json");

        let req = test::TestRequest::post()
            .uri("/auth/login")
            .set_json(&LoginRequest {
                email: "wrong_email".to_string(),
                password: random_password.to_string(),
            })
            .to_request();

        let resp = test::call_service(&app, req).await;
        assert_eq!(resp.status(), http::StatusCode::NOT_FOUND);

        let req = test::TestRequest::post()
            .uri("/auth/login")
            .set_json(&LoginRequest {
                email: random_email.clone(),
                password: "wrong_password".to_string(),
            })
            .to_request();

        let resp = test::call_service(&app, req).await;
        assert_eq!(resp.status(), http::StatusCode::UNAUTHORIZED);

        let mut conn = pool.get().await.expect("Couldn't get db connection from pool");
        diesel::delete(users::table.filter(users::email.eq(random_email)))
            .execute(&mut conn)
            .await
            .expect("Error deleting user");
    }
}
