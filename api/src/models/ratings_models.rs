use crate::schema::users;
use chrono::NaiveDateTime;
use diesel::Selectable;
use diesel::prelude::*;
use serde::{Deserialize, Serialize};

#[derive(Serialize, Selectable, Deserialize, Queryable)]
#[diesel(table_name = crate::schema::ratings)]
#[diesel(check_for_backend(diesel::pg::Pg))]
pub struct RatingResponse {
    pub id: i32,
    pub id_receiver: i32,
    pub id_sender: i32,
    pub stars: i32,
    pub comment: Option<String>,
    pub created_at: Option<NaiveDateTime>,
}

#[derive(Serialize, Deserialize)]
pub struct RatingResponseJson {
    pub id: i32,
    pub receiver: User,
    pub sender: User,
    pub stars: i32,
    pub comment: Option<String>,
    pub created_at: Option<NaiveDateTime>,
}

#[derive(Serialize, Deserialize, Queryable, Selectable, QueryableByName)]
pub struct User {
    pub id: i32,
    pub name: String,
    pub email: String,
}

#[derive(Deserialize, Serialize)]
pub struct RatingRequest {
    pub stars: i32,
    pub comment: Option<String>,
}

#[derive(Serialize, Deserialize, Insertable, Selectable)]
#[diesel(table_name = crate::schema::ratings)]
#[diesel(check_for_backend(diesel::pg::Pg))]
pub struct CreateRating {
    pub id_receiver: i32,
    pub id_sender: i32,
    pub stars: i32,
    pub comment: Option<String>,
}
