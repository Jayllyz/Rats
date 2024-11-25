// @generated automatically by Diesel CLI.

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
        id_user -> Int4,
        report_type -> Text,
        title -> Text,
        description -> Text,
        longitude -> Numeric,
        latitude -> Numeric,
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
        latitude -> Nullable<Numeric>,
        longitude -> Nullable<Numeric>,
    }
}

diesel::joinable!(reports -> users (id_user));

diesel::allow_tables_to_appear_in_same_query!(
    ratings,
    reports,
    users,
);
