use bigdecimal::BigDecimal;
use chrono::NaiveDateTime;
use diesel::prelude::*;
use diesel::Selectable;
use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Insertable)]
#[diesel(table_name = crate::schema::reports)]
pub struct CreateReport {
    pub id_user: i32,
    pub title: String,
    pub description: String,
    pub report_type: String,
    pub latitude: BigDecimal,
    pub longitude: BigDecimal,
}

#[derive(Serialize, Deserialize, Insertable, Selectable)]
#[diesel(table_name = crate::schema::reports)]
#[diesel(check_for_backend(diesel::pg::Pg))]
pub struct CreateRequest {
    pub title: String,
    pub description: String,
    pub report_type: String,
    pub latitude: BigDecimal,
    pub longitude: BigDecimal,
}

#[derive(Selectable, Serialize, Deserialize, Queryable)]
#[diesel(table_name = crate::schema::reports)]
#[diesel(check_for_backend(diesel::pg::Pg))]
pub struct ReportResponse {
    pub id: i32,
    pub id_user: Option<i32>,
    pub id_train_line: Option<i32>,
    pub title: String,
    pub description: String,
    pub report_type: String,
    pub latitude: BigDecimal,
    pub longitude: BigDecimal,
    pub created_at: Option<NaiveDateTime>,
}
