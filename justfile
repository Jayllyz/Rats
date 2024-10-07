#!/usr/bin/env -S just --justfile
# Install just cli :
# https://just.systems/man/en/installation.html

set windows-shell := ["powershell"]
set shell := ["bash", "-cu"]

_default:
  @just --list -u

alias r := ready
alias fr := format-rust
alias lr := lint-rust
alias la := lint-android

# Installs the tools recommended for this project
install-tools:
    cargo install cargo-binstall
    cargo binstall cargo-watch

format-rust:
    cd api && cargo fmt --all && cd ../

lint-rust:
    cd api && cargo clippy -- -D warnings && cd ../

lint-android:
    ./gradlew lint ktlintFormat

ready: 
    lint-android format-rust lint-rust
