use crate::db::DbPool;
use crate::models::users_models::{
    CreateUser, LoginRequest, LoginResponse, SignupRequest, UserResponse,
};
use crate::schema::users;
use actix_web::{post, web, HttpResponse, Result};
use diesel::prelude::*;
use jsonwebtoken::{encode, EncodingKey, Header};
use password_auth::{generate_hash, verify_password};
use serde::Serialize;
use std::time::{SystemTime, UNIX_EPOCH};

#[post("/signup")]
async fn signup(pool: web::Data<DbPool>, user: web::Json<SignupRequest>) -> Result<HttpResponse> {
    let password_hash = generate_hash(&user.password);

    let new_user =
        CreateUser { name: user.name.clone(), email: user.email.clone(), password: password_hash };

    let user_result = web::block(move || {
        let mut conn = pool.get().expect("Couldn't get db connection from pool");
        diesel::insert_into(users::table).values(&new_user).get_result::<UserResponse>(&mut conn)
    })
    .await
    .map_err(|e| actix_web::error::ErrorInternalServerError(format!("Database error: {}", e)))?;

    match user_result {
        Ok(user) => Ok(HttpResponse::Created().json(user)),
        Err(e) => Ok(HttpResponse::InternalServerError().json(format!("Error: {}", e))),
    }
}

#[post("/login")]
async fn login(
    pool: web::Data<DbPool>,
    credentials: web::Json<LoginRequest>,
) -> Result<HttpResponse> {
    let email = credentials.email.clone();
    let user = web::block(move || {
        let mut conn = pool.get().expect("Couldn't get db connection from pool");
        users::table.filter(users::email.eq(&email)).first::<UserResponse>(&mut conn)
    })
    .await
    .map_err(|e| actix_web::error::ErrorInternalServerError(format!("Database error: {}", e)))?;

    let user = match user {
        Ok(user) => user,
        Err(_e) => {
            return Err(actix_web::error::ErrorNotFound("User not found"));
        }
    };

    if verify_password(&credentials.password, &user.password).is_err() {
        return Err(actix_web::error::ErrorUnauthorized("Invalid credentials"));
    }

    let token = create_token(&user.id)
        .map_err(|_| actix_web::error::ErrorInternalServerError("An internal error occurred"))?;

    Ok(HttpResponse::Ok().json(LoginResponse { token }))
}

pub fn config_auth(cfg: &mut web::ServiceConfig) {
    cfg.service(web::scope("/auth").service(signup).service(login));
}

fn create_token(user_id: &i32) -> Result<String, jsonwebtoken::errors::Error> {
    #[derive(Serialize)]
    struct Claims {
        sub: i32,
        exp: u64,
    }

    let expiration =
        SystemTime::now().duration_since(UNIX_EPOCH).expect("Time went backwards").as_secs()
            + 24 * 60 * 60; // 24 hours from now

    let claims = Claims { sub: *user_id, exp: expiration };
    dotenvy::dotenv().ok();
    let secret = std::env::var("JWT_SECRET").unwrap_or_else(|_| "secure_secret_key".to_string());

    encode(&Header::default(), &claims, &EncodingKey::from_secret(secret.as_ref()))
}
