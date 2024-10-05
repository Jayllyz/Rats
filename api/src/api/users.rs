use crate::db::DbPool;
use crate::models::users_models::{CreateUser, UserResponse};
use crate::schema::users;
use actix_web::{get, post, web, HttpResponse, Result};
use diesel::prelude::*;

#[post("/users")]
pub async fn create_user(
    pool: web::Data<DbPool>,
    user: web::Json<CreateUser>,
) -> Result<HttpResponse> {
    let new_user = user.into_inner();

    let result = web::block(move || {
        let mut conn = pool.get().expect("Couldn't get db connection from pool");
        diesel::insert_into(users::table).values(&new_user).execute(&mut conn)
    })
    .await
    .expect("Error creating product");

    match result {
        Ok(_) => Ok(HttpResponse::Ok().json("User created")),
        Err(e) => {
            Ok(HttpResponse::InternalServerError().json(format!("Error creating user: {}", e)))
        }
    }
}

#[get("/users")]
pub async fn get_all_users(pool: web::Data<DbPool>) -> Result<HttpResponse> {
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
