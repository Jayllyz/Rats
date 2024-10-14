use crate::api::utils;
use crate::models::ratings_models::{CreateRating, RatingResponse};
use crate::schema::ratings;
use crate::{db::DbPool, models::ratings_models::RatingRequest};
use actix_web::{get, post, web, HttpRequest, HttpResponse, Result};
use diesel::prelude::*;
use diesel_async::RunQueryDsl;

#[get("")]
async fn get_ratings(pool: web::Data<DbPool>) -> Result<HttpResponse> {
    let mut conn = pool.get().await.expect("Couldn't get db connection from pool");

    let ratings = ratings::table
        .load::<RatingResponse>(&mut conn)
        .await
        .map_err(|_| actix_web::error::ErrorInternalServerError("Error querying ratings"))?;

    Ok(HttpResponse::Ok().json(ratings))
}

#[get("/{id}")]
async fn get_rating(pool: web::Data<DbPool>, id_rating: web::Path<i32>) -> Result<HttpResponse> {
    let mut conn = pool.get().await.expect("Couldn't get db connection from pool");

    let result =
        ratings::table.filter(ratings::id.eq(*id_rating)).first::<RatingResponse>(&mut conn).await;

    match result {
        Ok(rating) => Ok(HttpResponse::Ok().json(rating)),
        Err(diesel::result::Error::NotFound) => {
            Ok(HttpResponse::NotFound().body("Rating not found"))
        }
        Err(_) => Ok(HttpResponse::InternalServerError().finish()),
    }
}

#[get("/sender/{id}")]
async fn get_ratings_by_sender(
    pool: web::Data<DbPool>,
    id_sender: web::Path<i32>,
) -> Result<HttpResponse> {
    let mut conn = pool.get().await.expect("Couldn't get db connection from pool");

    let ratings = ratings::table
        .filter(ratings::id_sender.eq(*id_sender))
        .load::<RatingResponse>(&mut conn)
        .await
        .map_err(|_| actix_web::error::ErrorInternalServerError("Error querying ratings"))?;

    Ok(HttpResponse::Ok().json(ratings))
}

#[get("/receiver/{id}")]
async fn get_ratings_by_receiver(
    pool: web::Data<DbPool>,
    id_receiver: web::Path<i32>,
) -> Result<HttpResponse> {
    let mut conn = pool.get().await.expect("Couldn't get db connection from pool");

    let ratings = ratings::table
        .filter(ratings::id_receiver.eq(*id_receiver))
        .load::<RatingResponse>(&mut conn)
        .await
        .map_err(|_| actix_web::error::ErrorInternalServerError("Error querying ratings"))?;

    Ok(HttpResponse::Ok().json(ratings))
}

#[post("/{id}")]
async fn create_rating(
    pool: web::Data<DbPool>,
    id_receiver: web::Path<i32>,
    rating: web::Json<RatingRequest>,
    req: HttpRequest,
) -> Result<HttpResponse> {
    let id_user = match utils::validate_token(&req) {
        Ok(claims) => claims.sub,
        Err(err) => return Err(actix_web::error::ErrorUnauthorized(err)),
    };

    let new_rating = CreateRating {
        id_sender: id_user,
        id_receiver: *id_receiver,
        stars: rating.stars,
        comment: rating.comment.clone(),
    };

    let mut conn = pool.get().await.expect("Couldn't get db connection from pool");

    match diesel::insert_into(ratings::table)
        .values(&new_rating)
        .get_result::<RatingResponse>(&mut conn)
        .await
    {
        Ok(rating) => Ok(HttpResponse::Created().json(rating)),
        Err(e) => match e {
            diesel::result::Error::DatabaseError(
                diesel::result::DatabaseErrorKind::ForeignKeyViolation,
                _,
            ) => Err(actix_web::error::ErrorBadRequest("Invalid sender or receiver")),
            _ => {
                Err(actix_web::error::ErrorInternalServerError(format!("Database error: {:?}", e)))
            }
        },
    }
}

pub fn config_ratings(cfg: &mut web::ServiceConfig) {
    cfg.service(
        web::scope("/ratings")
            .service(get_ratings)
            .service(get_rating)
            .service(get_ratings_by_sender)
            .service(get_ratings_by_receiver)
            .service(create_rating),
    );
}
