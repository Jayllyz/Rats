use crate::db::DbPool;
use crate::models::users_models::UserResponse;
use crate::schema::users;
use actix_web::{get, web, HttpResponse, Result};
use diesel::prelude::*;

#[get("")]
async fn get_all_users(pool: web::Data<DbPool>) -> Result<HttpResponse> {
    let result = web::block(move || {
        let mut conn = pool.get().expect("Couldn't get db connection from pool");
        users::table.select(users::all_columns).load::<UserResponse>(&mut conn)
    })
    .await
    .expect("Error loading users");

    match result {
        Ok(users) => Ok(HttpResponse::Ok().json(users)),
        Err(e) => Ok(HttpResponse::InternalServerError().json(format!("Error: {}", e))),
    }
}

pub fn config_users(cfg: &mut web::ServiceConfig) {
    cfg.service(web::scope("/users").service(get_all_users));
}
