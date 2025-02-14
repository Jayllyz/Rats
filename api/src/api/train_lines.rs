use crate::api::utils;
use crate::db::DbPool;
use crate::models::train_lines_models::{
    QueryParams, Report, SelfReport, TrainLinesReports, TrainLinesResponse,
};
use crate::schema::reports;
use crate::schema::train_lines;
use crate::schema::users_lines;
use actix_web::{delete, get, post, web, HttpRequest, HttpResponse, Result};
use diesel::prelude::*;
use diesel_async::RunQueryDsl;

#[get("")]
async fn get_train_lines(
    pool: web::Data<DbPool>,
    query_params: web::Query<QueryParams>,
) -> Result<HttpResponse> {
    let mut conn = pool.get().await.expect("Couldn't get db connection from pool");

    let mut query = train_lines::table.into_boxed();

    if let Some(ref status) = query_params.status {
        query = query.filter(train_lines::status.eq(status));
    }

    if let Some(ref search) = query_params.search {
        query = query.filter(train_lines::name.ilike(format!("%{}%", search)));
    }

    let train_lines = query
        .select(TrainLinesResponse::as_select())
        .load::<TrainLinesResponse>(&mut conn)
        .await
        .map_err(actix_web::error::ErrorInternalServerError)?;

    Ok(HttpResponse::Ok().json(train_lines))
}

#[get("/{id}")]
async fn get_train_line(pool: web::Data<DbPool>, id_line: web::Path<i32>) -> Result<HttpResponse> {
    let mut conn = pool.get().await.expect("Couldn't get db connection from pool");

    let result = train_lines::table
        .filter(train_lines::id.eq(*id_line))
        .select(TrainLinesResponse::as_select())
        .first::<TrainLinesResponse>(&mut conn)
        .await;

    match result {
        Ok(report) => {
            let reports = reports::table
                .filter(reports::id_train_line.eq(*id_line))
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

            let subscribed = users_lines::table
                .filter(users_lines::id_line.eq(*id_line))
                .count()
                .get_result::<i64>(&mut conn)
                .await
                .map_err(actix_web::error::ErrorInternalServerError)?;

            Ok(HttpResponse::Ok().json(TrainLinesReports {
                id: report.id,
                name: report.name,
                status: report.status,
                subscribed: subscribed > 0,
                reports,
            }))
        }
        Err(diesel::result::Error::NotFound) => {
            Ok(HttpResponse::NotFound().body("Report not found"))
        }
        Err(_) => Ok(HttpResponse::InternalServerError().finish()),
    }
}

#[post("/subscribe/{id}")]
async fn subscribe_to_train_line(
    pool: web::Data<DbPool>,
    id_line: web::Path<i32>,
    req: HttpRequest,
) -> Result<HttpResponse> {
    let mut conn = pool.get().await.expect("Couldn't get db connection from pool");

    let id_user = match utils::validate_token(&req) {
        Ok(claims) => claims.sub,
        Err(err) => return Err(actix_web::error::ErrorUnauthorized(err)),
    };

    let result = diesel::insert_into(users_lines::table)
        .values((users_lines::id_user.eq(id_user), users_lines::id_line.eq(*id_line)))
        .execute(&mut conn)
        .await;

    match result {
        Ok(_) => Ok(HttpResponse::Ok().json("Subscribed")),
        Err(diesel::result::Error::DatabaseError(
            diesel::result::DatabaseErrorKind::UniqueViolation,
            _,
        )) => Ok(HttpResponse::Conflict().json("Already subscribed")),
        Err(_) => Ok(HttpResponse::InternalServerError().json("Error subscribing")),
    }
}

#[delete("/unsubscribe/{id}")]
async fn unsubscribe_to_train_line(
    pool: web::Data<DbPool>,
    id_line: web::Path<i32>,
    req: HttpRequest,
) -> Result<HttpResponse> {
    let mut conn = pool.get().await.expect("Couldn't get db connection from pool");

    let id_user = match utils::validate_token(&req) {
        Ok(claims) => claims.sub,
        Err(err) => return Err(actix_web::error::ErrorUnauthorized(err)),
    };

    let result = diesel::delete(
        users_lines::table
            .filter(users_lines::id_user.eq(id_user).and(users_lines::id_line.eq(*id_line))),
    )
    .execute(&mut conn)
    .await;

    match result {
        Ok(_) => Ok(HttpResponse::Ok().json("Unsubscribed")),
        Err(diesel::result::Error::DatabaseError(
            diesel::result::DatabaseErrorKind::UniqueViolation,
            _,
        )) => Ok(HttpResponse::Conflict().json("Not subscribed")),
        Err(_) => Ok(HttpResponse::InternalServerError().json("Error unsubscribing")),
    }
}

#[get("/self")]
async fn get_self_alerts(
    pool: web::Data<DbPool>,
    query_params: web::Query<QueryParams>,
    req: HttpRequest,
) -> Result<HttpResponse> {
    let mut conn = pool.get().await.expect("Couldn't get db connection from pool");

    let id_user = match utils::validate_token(&req) {
        Ok(claims) => claims.sub,
        Err(err) => return Err(actix_web::error::ErrorUnauthorized(err)),
    };

    let mut query = reports::table
        .inner_join(train_lines::table)
        .inner_join(users_lines::table.on(users_lines::id_line.eq(train_lines::id)))
        .filter(users_lines::id_user.eq(id_user))
        .into_boxed();

    if let Some(ref status) = query_params.status {
        query = query.filter(train_lines::status.eq(status));
    }

    let reports = query
        .select((
            train_lines::id,
            train_lines::name,
            reports::id,
            reports::title,
            reports::description,
            reports::report_type,
            reports::created_at,
        ))
        .load::<SelfReport>(&mut conn)
        .await
        .map_err(actix_web::error::ErrorInternalServerError)?;

    Ok(HttpResponse::Ok().json(reports))
}

pub fn config_train_lines(cfg: &mut web::ServiceConfig) {
    cfg.service(
        web::scope("/train_lines")
            .service(get_self_alerts)
            .service(subscribe_to_train_line)
            .service(get_train_lines)
            .service(get_train_line)
            .service(unsubscribe_to_train_line),
    );
}

#[cfg(test)]
mod tests {
    use super::*;
    use actix_web::{
        http::StatusCode,
        test::{self, TestRequest},
        App,
    };

    #[actix_web::test]
    async fn test_get_train_lines() {
        let pool = crate::db::establish_connection();
        let app = test::init_service(
            App::new().app_data(web::Data::new(pool)).configure(config_train_lines),
        )
        .await;

        let req = TestRequest::get().uri("/train_lines?status=safe").to_request();
        let resp = test::call_service(&app, req).await;

        assert_eq!(resp.status(), StatusCode::OK);
    }

    #[actix_web::test]
    async fn test_get_train_line() {
        let pool = crate::db::establish_connection();
        let app = test::init_service(
            App::new().app_data(web::Data::new(pool)).configure(config_train_lines),
        )
        .await;

        let req = TestRequest::get().uri("/train_lines/1").to_request();
        let resp = test::call_service(&app, req).await;

        assert_eq!(resp.status(), StatusCode::OK);
    }
}
