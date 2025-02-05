use crate::db::DbPool;
use crate::models::train_lines_models::{Report, TrainLinesReports, TrainLinesResponse};
use crate::schema::reports;
use crate::schema::train_lines;
use actix_web::{get, web, HttpResponse, Result};
use diesel::prelude::*;
use diesel_async::RunQueryDsl;

#[get("")]
async fn get_train_lines(pool: web::Data<DbPool>) -> Result<HttpResponse> {
    let mut conn = pool.get().await.expect("Couldn't get db connection from pool");

    let train_lines = train_lines::table
        .select(TrainLinesResponse::as_select())
        .load::<TrainLinesResponse>(&mut conn)
        .await
        .map_err(actix_web::error::ErrorInternalServerError)?;

    Ok(HttpResponse::Ok().json(train_lines))
}

#[get("/{id}")]
async fn get_train_line(
    pool: web::Data<DbPool>,
    id_report: web::Path<i32>,
) -> Result<HttpResponse> {
    let mut conn = pool.get().await.expect("Couldn't get db connection from pool");

    let result = train_lines::table
        .filter(train_lines::id.eq(*id_report))
        .select(TrainLinesResponse::as_select())
        .first::<TrainLinesResponse>(&mut conn)
        .await;

    match result {
        Ok(report) => {
            let reports = reports::table
                .filter(reports::id_train_line.eq(*id_report))
                .select((
                    reports::id,
                    reports::title,
                    reports::description,
                    reports::report_type,
                    reports::created_at,
                ))
                .load::<Report>(&mut conn)
                .await
                .map_err(actix_web::error::ErrorInternalServerError)?;

            Ok(HttpResponse::Ok().json(TrainLinesReports {
                id: report.id,
                name: report.name,
                status: report.status,
                reports,
            }))
        }
        Err(diesel::result::Error::NotFound) => {
            Ok(HttpResponse::NotFound().body("Report not found"))
        }
        Err(_) => Ok(HttpResponse::InternalServerError().finish()),
    }
}

pub fn config_train_lines(cfg: &mut web::ServiceConfig) {
    cfg.service(web::scope("/train_lines").service(get_train_lines).service(get_train_line));
}
