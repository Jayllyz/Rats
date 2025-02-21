use crate::api::utils;
use crate::db::DbPool;
use crate::models::messages::{CreateMessage, MessageResponse, PaginationQuery};
use crate::schema::messages;
use actix_web::{HttpRequest, HttpResponse, Result, get, post, web};
use diesel::prelude::*;
use diesel_async::RunQueryDsl;

#[get("")]
async fn get_messages(
    pool: web::Data<DbPool>,
    query: web::Query<PaginationQuery>,
) -> Result<HttpResponse> {
    let mut conn = pool.get().await.expect("Couldn't get db connection from pool");

    let mut query_builder = messages::table.into_boxed();

    if let Some(before_id) = query.before_id {
        query_builder = query_builder.filter(messages::id.lt(before_id));
    }

    let messages = query_builder
        .order_by(messages::id.desc())
        .limit(query.limit)
        .select(MessageResponse::as_select())
        .load::<MessageResponse>(&mut conn)
        .await
        .map_err(actix_web::error::ErrorInternalServerError)?;

    Ok(HttpResponse::Ok().json(messages))
}

#[post("")]
async fn create_message(
    pool: web::Data<DbPool>,
    message: web::Json<CreateMessage>,
    req: HttpRequest,
) -> Result<HttpResponse> {
    if message.content.is_empty() || message.content.len() > 1000 {
        return Err(actix_web::error::ErrorBadRequest(
            "Message length must be between 1 and 1000 characters",
        ));
    }

    let mut conn = pool.get().await.expect("Couldn't get db connection from pool");

    let id_sender = match utils::validate_token(&req) {
        Ok(claims) => claims.sub,
        Err(err) => return Err(actix_web::error::ErrorUnauthorized(err)),
    };

    let new_message = CreateMessage { content: message.content.clone(), id_sender };

    match diesel::insert_into(messages::table)
        .values(&new_message)
        .returning(MessageResponse::as_select())
        .get_result(&mut conn)
        .await
    {
        Ok(message) => Ok(HttpResponse::Created().json(message)),
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

pub fn config_messages(cfg: &mut web::ServiceConfig) {
    cfg.service(web::scope("/messages").service(get_messages).service(create_message));
}

#[cfg(test)]
mod tests {
    use super::*;
    use actix_web::{
        App,
        http::StatusCode,
        test::{self, TestRequest},
    };

    #[actix_web::test]
    async fn test_get_messages() {
        let pool = crate::db::establish_connection();
        let app = test::init_service(
            App::new().app_data(web::Data::new(pool)).configure(config_messages),
        )
        .await;

        let req = TestRequest::get().uri("/messages").to_request();
        let resp = test::call_service(&app, req).await;

        assert_eq!(resp.status(), StatusCode::OK);
    }
}
