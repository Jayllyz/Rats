FROM rust:1-slim

RUN apt update && \
    apt install -y libpq-dev --no-install-recommends

RUN cargo install diesel_cli --no-default-features --features postgres

WORKDIR /app

COPY . .

RUN cargo build --release --bin rats_api

CMD ["./target/release/rats_api"]
