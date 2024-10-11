// @generated automatically by Diesel CLI.

diesel::table! {
    ratings (id) {
        id -> Int4,
        user_id -> Int4,
        stars -> Int4,
        comment -> Nullable<Text>,
        created_at -> Nullable<Timestamp>,
    }
}

diesel::table! {
    users (id) {
        id -> Int4,
        name -> Varchar,
        email -> Varchar,
        password -> Varchar,
        token -> Nullable<Varchar>,
    }
}

diesel::allow_tables_to_appear_in_same_query!(
    ratings,
    users,
);
