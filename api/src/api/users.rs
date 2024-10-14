use crate::db::DbPool;
use crate::models::users_models::UserResponse;
use crate::models::users_models::SelfResponse;
use crate::schema::users;
use crate::api::utils;
use actix_web::{get, web, HttpRequest, HttpResponse, Result};
use diesel::prelude::*;
use diesel_async::RunQueryDsl;
use crate::pagination::*;
use serde_json::json;

#[get("")]
async fn get_all_users(
    pool: web::Data<DbPool>,
    params: web::Query<PaginationParams>,
) -> Result<HttpResponse> {
    let mut conn = pool.get().await.expect("Couldn't get db connection from pool");

    let result = users::table
        .select(users::all_columns)
        .order(users::id)
        .paginate(params.page)
        .page_size(params.page_size)
        .load_and_count_pages::<UserResponse>(&mut conn)
        .await;

    match result {
        Ok((user_data, total_pages)) => Ok(HttpResponse::Ok().json({
            json!({
                "users": user_data,
                "total_pages": total_pages,
            })
        })),
        Err(_) => Ok(HttpResponse::InternalServerError().finish()),
    }
}

#[get("/{id}")]
async fn get_user(pool: web::Data<DbPool>, id_user: web::Path<i32>) -> Result<HttpResponse> {
    let mut conn = pool.get().await.expect("Couldn't get db connection from pool");

    let result = users::table
        .filter(users::id.eq(*id_user))
        .first::<UserResponse>(&mut conn)
        .await;

    match result {
        Ok(user) => Ok(HttpResponse::Ok().json(user)),
        Err(diesel::result::Error::NotFound) => Ok(HttpResponse::NotFound().body("User not found")),
        Err(_) => Ok(HttpResponse::InternalServerError().finish()),
    }
}

#[get("")]
async fn get_self(pool: web::Data<DbPool>, req: HttpRequest) -> Result<HttpResponse> {
    let id_user = match utils::validate_token(&req) {
        Ok(claims) => claims.sub,
        Err(err) => return Err(actix_web::error::ErrorUnauthorized(err)),
    };

    let mut conn = pool.get().await.expect("Couldn't get db connection from pool");

    let result = users::table
        .filter(users::id.eq(id_user))
        .first::<UserResponse>(&mut conn)
        .await;

    match result {
        Ok(user) => {
            let user_response = SelfResponse {
                id: user.id,
                name: user.name.clone(),
                email: user.email.clone(),
            };

            Ok(HttpResponse::Ok().json(user_response))
        },
        Err(diesel::result::Error::NotFound) => Ok(HttpResponse::NotFound().body("User not found")),
        Err(_) => Ok(HttpResponse::InternalServerError().finish()),
    }
}

pub fn config_users(cfg: &mut web::ServiceConfig) {
    cfg.service(web::scope("/users").service(get_all_users).service(get_user));
    cfg.service(web::scope("/self").service(get_self));
}


#[cfg(test)]
mod tests {
    use super::*;
    use actix_web::{http, test, App};

    #[actix_web::test]
    async fn get_all_users() {
        let pool = crate::db::establish_connection();
        let app =
            test::init_service(App::new().configure(config_users).app_data(web::Data::new(pool)))
                .await;
        let req = test::TestRequest::get().uri("/users").to_request();
        let resp = test::call_service(&app, req).await;

        assert_eq!(resp.status(), http::StatusCode::OK);
    }

    #[actix_web::test]
    async fn get_all_users_with_pagination() {
        let pool = crate::db::establish_connection();
        let app =
            test::init_service(App::new().configure(config_users).app_data(web::Data::new(pool)))
                .await;
        let req = test::TestRequest::get().uri("/users?page=1&page_size=1").to_request();
        let resp = test::call_service(&app, req).await;

        assert_eq!(resp.status(), http::StatusCode::OK);
    }

    #[actix_web::test]
    async fn get_all_users_with_invalid_pagination_values() {
        let pool = crate::db::establish_connection();
        let app =
            test::init_service(App::new().configure(config_users).app_data(web::Data::new(pool)))
                .await;
        let req = test::TestRequest::get().uri("/users?page=0&page_size=-20").to_request();
        let resp = test::call_service(&app, req).await;

        assert_eq!(resp.status(), http::StatusCode::OK);
    }

    #[actix_web::test]
    async fn get_all_users_with_invalid_pagination() {
        let pool = crate::db::establish_connection();
        let app =
            test::init_service(App::new().configure(config_users).app_data(web::Data::new(pool)))
                .await;
        let req = test::TestRequest::get().uri("/users?page=test&page_size=test").to_request();
        let resp = test::call_service(&app, req).await;

        assert_eq!(resp.status(), http::StatusCode::BAD_REQUEST);
    }
}
