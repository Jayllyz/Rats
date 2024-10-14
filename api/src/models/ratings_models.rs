use diesel::prelude::*;
use diesel::Selectable;
use serde::{Deserialize, Serialize};
use chrono::NaiveDateTime;

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

#[derive(Deserialize)]
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