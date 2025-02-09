// @generated automatically by Diesel CLI.

diesel::table! {
    messages (id) {
        id -> Int4,
        content -> Text,
        id_sender -> Int4,
        created_at -> Timestamptz,
        updated_at -> Timestamptz,
    }
}

diesel::table! {
    ratings (id) {
        id -> Int4,
        id_receiver -> Int4,
        id_sender -> Int4,
        stars -> Int4,
        comment -> Nullable<Text>,
        created_at -> Nullable<Timestamp>,
    }
}

diesel::table! {
    reports (id) {
        id -> Int4,
        id_user -> Nullable<Int4>,
        report_type -> Text,
        title -> Text,
        description -> Text,
        longitude -> Numeric,
        latitude -> Numeric,
        created_at -> Nullable<Timestamp>,
        id_train_line -> Nullable<Int4>,
    }
}

diesel::table! {
    train_lines (id) {
        id -> Int4,
        name -> Text,
        status -> Text,
    }
}

diesel::table! {
    users (id) {
        id -> Int4,
        name -> Varchar,
        email -> Varchar,
        password -> Varchar,
        token -> Nullable<Varchar>,
        latitude -> Nullable<Numeric>,
        longitude -> Nullable<Numeric>,
    }
}

diesel::table! {
    users_lines (id_user, id_line) {
        id_user -> Int4,
        id_line -> Int4,
    }
}

diesel::joinable!(messages -> users (id_sender));
diesel::joinable!(reports -> train_lines (id_train_line));
diesel::joinable!(reports -> users (id_user));
diesel::joinable!(users_lines -> train_lines (id_line));
diesel::joinable!(users_lines -> users (id_user));

diesel::allow_tables_to_appear_in_same_query!(
    messages,
    ratings,
    reports,
    train_lines,
    users,
    users_lines,
);
