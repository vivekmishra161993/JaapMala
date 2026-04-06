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

## Screenshots
*(Images to be added)*
