# AGENTS.md

This file contains a summary of the project's structure, key technologies, and build process.

## Project Structure

The project is a Kotlin Multiplatform project with the following structure:

- **`composeApp`**: This is the main application module, which contains the shared UI and business logic for the Android and iOS apps.
- **`platform`**: This is a multi-module library that provides platform-specific implementations for features like data storage, networking, and domain logic.
- **`build-logic`**: This directory contains custom Gradle plugins that are used to configure the build process.
- **`iosApp`**: This directory contains the iOS-specific code and configuration.

## Key Technologies

The project uses the following key technologies:

- **Kotlin Multiplatform**: The project is built using Kotlin Multiplatform, which allows for sharing code between Android and iOS.
- **Jetpack Compose**: The UI is built using Jetpack Compose, a modern declarative UI toolkit for Android.
- **Gradle**: The project is built using Gradle, a popular build automation tool.
- **Koin**: The project uses Koin for dependency injection.

## Build Process

The project is built using a custom Gradle plugin located in the `build-logic` directory. This plugin configures the build process for the `composeApp` and `platform` modules.
