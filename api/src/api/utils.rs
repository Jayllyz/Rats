use crate::models::users_models::UserResponse;
use crate::schema::users;
use actix_web::{HttpRequest, Result};
use diesel::prelude::*;
use diesel_async::pooled_connection::deadpool;
use diesel_async::AsyncPgConnection;
use diesel_async::RunQueryDsl;
use jsonwebtoken::{decode, DecodingKey, Validation};
use jsonwebtoken::{encode, EncodingKey, Header};
use serde::Deserialize;
use serde::Serialize;
use std::env;
use std::time::{SystemTime, UNIX_EPOCH};

#[derive(Debug, Serialize, Deserialize)]
pub struct Claims {
    pub sub: i32,
    exp: u64,
}

pub fn create_token(user_id: &i32) -> Result<String, jsonwebtoken::errors::Error> {
    let expiration =
        SystemTime::now().duration_since(UNIX_EPOCH).expect("Time went backwards").as_secs()
            + 24 * 60 * 60; // 24 hours from now

    let claims = Claims { sub: *user_id, exp: expiration };
    dotenvy::dotenv().ok();
    let secret = std::env::var("JWT_SECRET").unwrap_or_else(|_| "secure_secret_key".to_string());

    encode(&Header::default(), &claims, &EncodingKey::from_secret(secret.as_ref()))
}

pub fn validate_token(req: &HttpRequest) -> Result<Claims, actix_web::Error> {
    let auth_header = req.headers().get("Authorization");

    if auth_header.is_none() {
        return Err(actix_web::error::ErrorUnauthorized("Missing token"));
    }

    let header_value = auth_header.unwrap();

    let header_str = match header_value.to_str() {
        Ok(s) => s,
        Err(_) => return Err(actix_web::error::ErrorUnauthorized("Invalid token")),
    };

    if !header_str.starts_with("Bearer ") {
        return Err(actix_web::error::ErrorUnauthorized("Invalid token"));
    }

    let token = &header_str[7..]; // Strip le "Bearer "

    let secret = env::var("JWT_SECRET").unwrap_or_else(|_| "secure_secret_key".to_string());
    let decoding_key = DecodingKey::from_secret(secret.as_ref());

    let mut validation = Validation::default();
    validation.validate_exp = true; // Check si le token a expiré

    let token_data = decode::<Claims>(token, &decoding_key, &validation)
        .map_err(|_| actix_web::error::ErrorUnauthorized("Invalid token"))?;

    Ok(token_data.claims)
}

pub async fn user_exists(
    conn: &mut deadpool::Object<AsyncPgConnection>,
    id_user: i32,
) -> Result<(), actix_web::Error> {
    let _user = users::table
        .filter(users::id.eq(id_user))
        .first::<UserResponse>(conn)
        .await
        .map_err(|e| match e {
            diesel::result::Error::NotFound => actix_web::error::ErrorNotFound("User not found"),
            _ => actix_web::error::ErrorInternalServerError(format!("Database error: {}", e)),
        })?;

    Ok(())
}
