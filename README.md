# Jaap Mala – Android Architecture Showcase

## Overview
A production-grade Android application designed to demonstrate scalable architecture, offline-first design, and modern Android development practices for spiritual practice tracking.

## Tech Stack
- **Kotlin**: Primary programming language.
- **Jetpack Compose**: Declarative UI toolkit for modern, native layouts.
- **Hilt (Dependency Injection)**: Industry-standard DI for better testability and decoupled components.
- **Coroutines + Flow**: Efficient asynchronous programming and reactive data streams.
- **Room Database**: Robust local data persistence following offline-first principles.
- **DataStore**: Modern, type-safe alternative to SharedPreferences for user settings.

## Architecture
- **Clean Architecture**: Strictly separated into Domain, Data, and Presentation layers to ensure high maintainability.
- **MVVM Pattern**: Facilitates a clear separation between UI logic and business logic.
- **Reactive UI**: Implements Unidirectional Data Flow (UDF) using Compose and StateFlow.

## Key Features
- **Customizable Mala Size**: Flexibility to define bead counts for different spiritual practices.
- **Daily Reset Logic**: Intelligent tracking of daily progress and mala completions.
- **Offline-first Design**: Reliable performance without internet dependency via local SQLite persistence.
- **Haptic & Audio Feedback**: Configurable vibration and sound settings for an immersive chanting experience.

## Engineering Decisions
- **Why Clean Architecture?**: To achieve a high degree of testability and allow the business logic (Domain) to remain independent of UI or Database frameworks.
- **Why Hilt?**: To standardize dependency injection across the project, leveraging Hilt's seamless integration with ViewModels and the Android lifecycle.
- **State Management**: Utilizing `StateFlow` to provide a robust, lifecycle-aware stream of UI states, ensuring the UI always reflects the current data state.
  <img src="screenshots/home.png" width="30%" />
## Screenshots
<img width="30%" alt="Screenshot_20250927-175426" src="https://github.com/user-attachments/assets/43c8a75d-f04b-4821-b401-7f26e7b05f54" />
<img width="30%" alt="Screenshot_20250927-175437" src="https://github.com/user-attachments/assets/e26d398b-7830-4007-9d14-7257fc59dd99" />
<img width="30%" alt="Screenshot_20250927-175446" src="https://github.com/user-attachments/assets/6f411cf0-5f8f-47cd-8b02-c50bafddb00f" />
<img width="30%" alt="Screenshot_20250927-175454" src="https://github.com/user-attachments/assets/c32db8ab-b512-4c16-8536-e1fd143510fd" />
<img width="30%" alt="Screenshot_20250927-175418" src="https://github.com/user-attachments/assets/d01fa3fc-4a0c-41cc-91d2-d5c7c5741bc0" />

