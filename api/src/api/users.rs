use crate::db::DbPool;
use crate::models::users_models::UserResponse;
use crate::schema::users;
use actix_web::{get, web, HttpResponse, Result};
use diesel::prelude::*;

#[get("")]
async fn get_all_users(pool: web::Data<DbPool>) -> Result<HttpResponse> {
    let mut conn = pool.get().expect("Couldn't get db connection from pool");

    let users = users::table
        .load::<UserResponse>(&mut conn)
        .map_err(|_| actix_web::error::ErrorInternalServerError("Error querying users"))?;

    Ok(HttpResponse::Ok().json(users))
}

pub fn config_users(cfg: &mut web::ServiceConfig) {
    cfg.service(web::scope("/users").service(get_all_users));
}

#[cfg(test)]
mod tests {
    use super::*;
    use actix_web::{http, test, App};

    #[actix_web::test]
    async fn test_get_all_users() {
        let pool = crate::db::establish_connection();
        let app =
            test::init_service(App::new().configure(config_users).app_data(web::Data::new(pool)))
                .await;
        let req = test::TestRequest::get().uri("/users").to_request();
        let resp = test::call_service(&app, req).await;

        assert_eq!(resp.status(), http::StatusCode::OK);
    }
}
