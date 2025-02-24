use crate::api::utils;
use crate::db::DbPool;
use crate::models::reports_models::{CreateReport, CreateRequest, ReportResponse};
use crate::models::users_models::UserResponse;
use crate::schema::{reports, users};
use actix_web::{HttpRequest, HttpResponse, Result, get, post, web};
use bigdecimal::{BigDecimal, ToPrimitive};
use diesel::prelude::*;
use diesel_async::RunQueryDsl;

#[get("")]
async fn get_reports(pool: web::Data<DbPool>) -> Result<HttpResponse> {
    let mut conn = pool.get().await.expect("Couldn't get db connection from pool");

    let reports = reports::table
        .select(ReportResponse::as_select())
        .load::<ReportResponse>(&mut conn)
        .await
        .map_err(actix_web::error::ErrorInternalServerError)?;

    Ok(HttpResponse::Ok().json(reports))
}

#[get("/{id}")]
async fn get_report(pool: web::Data<DbPool>, id_report: web::Path<i32>) -> Result<HttpResponse> {
    let mut conn = pool.get().await.expect("Couldn't get db connection from pool");

    let result = reports::table
        .filter(reports::id.eq(*id_report))
        .select(ReportResponse::as_select())
        .first::<ReportResponse>(&mut conn)
        .await;

    match result {
        Ok(report) => Ok(HttpResponse::Ok().json(report)),
        Err(diesel::result::Error::NotFound) => {
            Ok(HttpResponse::NotFound().body("Report not found"))
        }
        Err(_) => Ok(HttpResponse::InternalServerError().finish()),
    }
}

#[get("/nearby")]
async fn get_nearby_reports(pool: web::Data<DbPool>, req: HttpRequest) -> Result<HttpResponse> {
    let id_user = match utils::validate_token(&req) {
        Ok(claims) => claims.sub,
        Err(err) => return Err(actix_web::error::ErrorUnauthorized(err)),
    };

    let mut conn = pool.get().await.expect("Couldn't get db connection from pool");

    let user = match users::table
        .filter(users::id.eq(id_user))
        .select(UserResponse::as_select())
        .first::<UserResponse>(&mut conn)
        .await
    {
        Ok(user) => user,
        Err(diesel::result::Error::NotFound) => {
            return Err(actix_web::error::ErrorNotFound("User not found"));
        }
        Err(_) => return Err(actix_web::error::ErrorInternalServerError("Database error")),
    };

    if user.latitude.is_none() || user.longitude.is_none() {
        return Err(actix_web::error::ErrorBadRequest("User location not available"));
    }

    let all_reports = match reports::table
        .select(ReportResponse::as_select())
        .load::<ReportResponse>(&mut conn)
        .await
    {
        Ok(reports) => reports,
        Err(_) => {
            return Err(actix_web::error::ErrorInternalServerError("Error querying reports"));
        }
    };

    let nearby_reports = all_reports
        .into_iter()
        .filter(|report| {
            let user_lat = user.latitude.clone().unwrap().to_f64().unwrap();
            let user_lon = user.longitude.clone().unwrap().to_f64().unwrap();

            let report_lat = report.latitude.to_f64().unwrap();
            let report_lon = report.longitude.to_f64().unwrap();

            let distance = utils::haversine_distance(user_lat, user_lon, report_lat, report_lon);
            distance <= 5.0 // 5 km
        })
        .collect::<Vec<ReportResponse>>();

    Ok(HttpResponse::Ok().json(nearby_reports))
}

#[post("")]
async fn create_report(
    pool: web::Data<DbPool>,
    report: web::Json<CreateRequest>,
    req: HttpRequest,
) -> Result<HttpResponse> {
    let mut conn = pool.get().await.expect("Couldn't get db connection from pool");

    let id_user = match utils::validate_token(&req) {
        Ok(claims) => claims.sub,
        Err(err) => return Err(actix_web::error::ErrorUnauthorized(err)),
    };

    let new_report = CreateReport {
        id_user,
        title: report.title.clone(),
        description: report.description.clone(),
        report_type: report.report_type.clone(),
        latitude: report.latitude.clone() as BigDecimal,
        longitude: report.longitude.clone() as BigDecimal,
    };

    match diesel::insert_into(reports::table)
        .values(&new_report)
        .returning(ReportResponse::as_select())
        .get_result(&mut conn)
        .await
    {
        Ok(report) => Ok(HttpResponse::Created().json(report)),
        Err(e) => match e {
            diesel::result::Error::DatabaseError(
                diesel::result::DatabaseErrorKind::ForeignKeyViolation,
                _,
            ) => Err(actix_web::error::ErrorBadRequest("User not found")),
            _ => {
                Err(actix_web::error::ErrorInternalServerError(format!("Database error: {:?}", e)))
            }
        },
    }
}

pub fn config_reports(cfg: &mut web::ServiceConfig) {
    cfg.service(
        web::scope("/reports")
            .service(get_reports)
            .service(get_nearby_reports)
            .service(get_report)
            .service(create_report),
    );
}
