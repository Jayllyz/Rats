use bigdecimal::BigDecimal;
use chrono::NaiveDateTime;
use diesel::prelude::*;
use diesel::Selectable;
use serde::{Deserialize, Serialize};
use diesel::sql_types::Text;
use diesel::deserialize::FromSql;
use diesel::serialize::{ToSql, Output};
use diesel::pg::Pg;
use std::io::Write;

// FIXME: enum
#[derive(Debug, Serialize, Deserialize, diesel_derive_enum::DbEnum, Clone)]
#[ExistingTypePath = "crate::schema::sql_types::ReportType"]
#[serde(rename_all = "PascalCase")]
pub enum ReportType {
    DangerousIndividual,
    Pickpocket,
    Accident,
    Other,
}

impl FromSql<Text, Pg> for ReportType {
    fn from_sql(bytes: diesel::pg::PgValue) -> diesel::deserialize::Result<Self> {
        let value = std::str::from_utf8(bytes.as_bytes())
            .map_err(|_| "Unable to convert to UTF-8")?
            .trim()
            .to_lowercase();

        match value.as_str() {
            "individu dangereux" => Ok(ReportType::DangerousIndividual),
            "pickpocket" => Ok(ReportType::Pickpocket),
            "accident" => Ok(ReportType::Accident),
            "autre" => Ok(ReportType::Other),
            _ => Err("Unrecognized enum variant".into()),
        }
    }
}

impl ToSql<Text, Pg> for ReportType {
    fn to_sql(&self, out: &mut Output<Pg>) -> diesel::serialize::Result {
        let value = match self {
            ReportType::DangerousIndividual => "individu dangereux",
            ReportType::Pickpocket => "pickpocket",
            ReportType::Accident => "accident",
            ReportType::Other => "autre",
        };

        out.write_all(value.as_bytes())?;
        Ok(diesel::serialize::IsNull::No)
    }
}

#[derive(Serialize, Deserialize, Insertable, Selectable)]
#[diesel(table_name = crate::schema::reports)]
#[diesel(check_for_backend(diesel::pg::Pg))]
pub struct CreateReport {
    pub id_user: i32,
    pub title: String,
    pub description: String,
    pub report_type: ReportType,
    pub latitude: BigDecimal,
    pub longitude: BigDecimal,
}

#[derive(Serialize, Deserialize)]
pub struct CreateRequest {
    pub title: String,
    pub description: String,
    pub report_type: ReportType,
    pub latitude: BigDecimal,
    pub longitude: BigDecimal,
}

#[derive(Selectable, Serialize, Deserialize, Queryable)]
#[diesel(table_name = crate::schema::reports)]
#[diesel(check_for_backend(diesel::pg::Pg))]
pub struct ReportResponse {
    pub id: i32,
    pub title: String,
    pub description: String,
    pub report_type: ReportType,
    pub latitude: BigDecimal,
    pub longitude: BigDecimal,
    pub created_at: Option<NaiveDateTime>,
}