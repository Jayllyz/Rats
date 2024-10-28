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
    users::table
        .filter(users::id.eq(id_user))
        .select(UserResponse::as_select())
        .first::<UserResponse>(conn)
        .await
        .map_err(|e| match e {
            diesel::result::Error::NotFound => actix_web::error::ErrorNotFound("User not found"),
            _ => actix_web::error::ErrorInternalServerError(format!("Database error: {}", e)),
        })?;

    Ok(())
}

pub fn haversine_distance(lat1: f64, lon1: f64, lat2: f64, lon2: f64) -> f64 {
    const EARTH_RADIUS_METERS: f64 = 6_371_000.0;

    let d_lat = (lat2 - lat1).to_radians();
    let d_lon = (lon2 - lon1).to_radians();

    let angle = (d_lat / 2.0).sin() * (d_lat / 2.0).sin()
        + lat1.to_radians().cos()
            * lat2.to_radians().cos()
            * (d_lon / 2.0).sin()
            * (d_lon / 2.0).sin();

    let coords = 2.0 * angle.sqrt().atan2((1.0 - angle).sqrt());

    EARTH_RADIUS_METERS * coords
}

#[allow(dead_code)] // Used in tests
pub async fn user_id_by_email(
    conn: &mut deadpool::Object<AsyncPgConnection>,
    email: &str,
) -> Result<i32, actix_web::Error> {
    let user = users::table
        .filter(users::email.eq(email))
        .select(UserResponse::as_select())
        .first::<UserResponse>(conn)
        .await
        .map_err(|e| match e {
            diesel::result::Error::NotFound => actix_web::error::ErrorNotFound("User not found"),
            _ => actix_web::error::ErrorInternalServerError(format!("Database error: {}", e)),
        })?;

    Ok(user.id)
}

#[cfg(test)]
mod tests {
    use super::*;
    #[test]
    fn test_same_point() {
        let point_lattitude = 44.9;
        let point_lontitude = 56.7;
        assert_eq!(
            haversine_distance(point_lattitude, point_lontitude, point_lattitude, point_lontitude),
            0.0
        );
    }

    #[test]
    fn test_known_distance() {
        // Paris to London coordinates
        let paris_lat = 48.8566;
        let paris_lon = 2.3522;
        let london_lat = 51.5074;
        let london_lon = -0.1278;

        let expected_meters = 344_000.0;
        let calculated = haversine_distance(paris_lat, paris_lon, london_lat, london_lon);

        assert!((calculated - expected_meters).abs() < expected_meters * 0.05);
    }
}
