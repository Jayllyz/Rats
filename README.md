# RATS Project

[![CI Rust](https://github.com/Jayllyz/Rats/actions/workflows/ci-rust.yaml/badge.svg)](https://github.com/Jayllyz/Rats/actions/workflows/ci-rust.yaml)
[![CI Android](https://github.com/Jayllyz/Rats/actions/workflows/ci-android.yaml/badge.svg)](https://github.com/Jayllyz/Rats/actions/workflows/ci-android.yaml)

Android kotlin Mobile App to learn MVVM architecture and mobile development.

## Overview

RATS (Railway Assistance and Tracking System) helps users:

- Track their location on a map
- View nearby users in the same train/wagon
- Report incidents and safety concerns
- Rate and communicate with other passengers
- Follow specific train lines and receive alerts

## Technical Stack

- **Mobile App**: Kotlin for Android
- **Backend**: Rust API (Actix Web)
- **Database**: PostgreSQL 17

## Features

- Real-time location tracking
- User profile management
- In-app messaging
- Safety reporting system
- Line tracking and notifications

## Getting Started

1. Clone the repository
2. Set up your Google Maps API key in `local.properties`
3. Run the app using Android Studio or Gradle:

```bash
./gradlew assembleRelease
```

## License

This project is licensed under the MIT License.
