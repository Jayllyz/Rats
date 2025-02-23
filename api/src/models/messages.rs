use chrono::{DateTime, Utc};
use diesel::{prelude::*, Selectable};
use serde::{Deserialize, Serialize};

#[derive(Serialize, Selectable, Deserialize, Queryable)]
#[diesel(table_name = crate::schema::messages)]
#[diesel(check_for_backend(diesel::pg::Pg))]
pub struct MessageResponse {
    pub id: i32,
    pub content: String,
    pub id_sender: i32,
    pub created_at: DateTime<Utc>,
    pub updated_at: DateTime<Utc>,
}

#[derive(Serialize, Deserialize, Insertable, Selectable)]
#[diesel(table_name = crate::schema::messages)]
#[diesel(check_for_backend(diesel::pg::Pg))]
pub struct CreateMessage {
    pub content: String,
    pub id_sender: i32,
}

#[derive(Deserialize)]
pub struct PaginationQuery {
    pub before_id: Option<i32>,
    #[serde(default = "default_limit")]
    pub limit: i64,
}

fn default_limit() -> i64 {
    50
}
