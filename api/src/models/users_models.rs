use bigdecimal::BigDecimal;
use diesel::Selectable;
use diesel::prelude::*;
use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Insertable, Selectable)]
#[diesel(table_name = crate::schema::users)]
#[diesel(check_for_backend(diesel::pg::Pg))]
pub struct CreateUser {
    pub name: String,
    pub email: String,
    pub password: String,
}

#[derive(Serialize, Selectable, Deserialize, Queryable)]
#[diesel(table_name = crate::schema::users)]
#[diesel(check_for_backend(diesel::pg::Pg))]
pub struct FullUserResponse {
    pub id: i32,
    pub name: String,
    pub email: String,
    pub password: String,
    pub latitude: Option<BigDecimal>,
    pub longitude: Option<BigDecimal>,
    pub token: Option<String>,
    pub created_at: Option<chrono::NaiveDateTime>,
}

#[derive(Serialize, Selectable, Deserialize, Queryable)]
#[diesel(table_name = crate::schema::users)]
#[diesel(check_for_backend(diesel::pg::Pg))]
pub struct UserResponse {
    pub id: i32,
    pub name: String,
    pub email: String,
    pub latitude: Option<BigDecimal>,
    pub longitude: Option<BigDecimal>,
    pub token: Option<String>,
    pub created_at: Option<chrono::NaiveDateTime>,
}

#[derive(Deserialize)]
pub struct SignupRequest {
    pub name: String,
    pub email: String,
    pub password: String,
}

#[derive(Deserialize, Serialize)]
pub struct LoginRequest {
    pub email: String,
    pub password: String,
}

#[derive(Serialize)]
pub struct LoginResponse {
    pub token: String,
}

#[derive(Serialize)]
pub struct SelfResponse {
    pub id: i32,
    pub name: String,
    pub email: String,
    pub rating: f64,
    pub rating_count: usize,
    pub created_at: Option<chrono::NaiveDateTime>,
}
#[derive(Deserialize, Serialize)]
pub struct PositionRequest {
    pub latitude: BigDecimal,
    pub longitude: BigDecimal,
}
