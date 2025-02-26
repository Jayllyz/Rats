use chrono::NaiveDateTime;
use diesel::Selectable;
use diesel::prelude::*;
use serde::{Deserialize, Serialize};

#[derive(Serialize, Selectable, Deserialize, Queryable, Debug)]
#[diesel(table_name = crate::schema::train_lines)]
#[diesel(check_for_backend(diesel::pg::Pg))]
pub struct TrainLinesResponse {
    pub id: i32,
    pub name: String,
    pub status: String,
}

#[derive(Deserialize)]
pub struct QueryParams {
    pub status: Option<String>,
    pub search: Option<String>,
}

#[derive(Serialize, Deserialize)]
pub struct TrainLinesReports {
    pub id: i32,
    pub name: String,
    pub status: String,
    pub subscribed: bool,
    pub reports: Vec<Report>,
}

#[derive(Serialize, Deserialize, Queryable, Selectable)]
#[diesel(table_name = crate::schema::reports)]
pub struct Report {
    pub id: i32,
    pub title: String,
    pub description: String,
    pub report_type: String,
    pub created_at: Option<NaiveDateTime>,
}

#[derive(Serialize, Deserialize, Queryable)]
pub struct SelfReport {
    pub id_line: i32,
    pub line_name: String,
    pub id_report: i32,
    pub title: String,
    pub description: String,
    pub report_type: String,
    pub created_at: Option<NaiveDateTime>,
}
