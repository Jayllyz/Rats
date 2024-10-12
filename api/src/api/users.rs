use crate::db::DbPool;
use crate::models::users_models::UserResponse;
use crate::pagination::*;
use crate::schema::users;
use actix_web::{get, web, HttpResponse, Result};
use diesel::prelude::*;
use serde_json::json;

#[get("")]
async fn get_all_users(
    pool: web::Data<DbPool>,
    params: web::Query<PaginationParams>,
) -> Result<HttpResponse> {
    let mut conn = pool.get().await.expect("Couldn't get db connection from pool");

    let query = users::table
        .select(users::all_columns)
        .order(users::id)
        .paginate(params.page)
        .page_size(params.page_size);

    let result = query.load_and_count_pages::<UserResponse>(&mut conn).await;

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

pub fn config_users(cfg: &mut web::ServiceConfig) {
    cfg.service(web::scope("/users").service(get_all_users));
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
