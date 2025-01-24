FROM rust:1-slim

ARG PORT
ENV PORT=${PORT}
EXPOSE ${PORT}

RUN apt-get update && \
    apt-get install -y libpq-dev --no-install-recommends

RUN cargo install diesel_cli --no-default-features --features postgres

WORKDIR /app

COPY . .

RUN cargo build --release --bin rats_api

CMD ["./target/release/rats_api"]
