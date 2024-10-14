use actix_web::{HttpRequest, Result};
use jsonwebtoken::{decode, DecodingKey, Validation};
use serde::Deserialize;
use std::env;

#[derive(Debug, Deserialize)]
pub struct Claims {
    pub sub: i32,
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
