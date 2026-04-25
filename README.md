# HyperDict

<div align="center">

[![Android CI/CD](https://github.com/ChidcGithub/HyperDict/actions/workflows/android-build.yml/badge.svg)](https://github.com/ChidcGithub/HyperDict/actions/workflows/android-build.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=26)
[![Version](https://img.shields.io/badge/version-0.0.1-blue.svg)](https://github.com/ChidcGithub/HyperDict/releases)

A modern dictionary application for language learners, translators, and reading enthusiasts.

[Download APK](https://github.com/ChidcGithub/HyperDict/releases) | [Features](#features) | [Build Instructions](#build-instructions)

</div>

---

## Overview

HyperDict is an Android dictionary application that combines offline and online capabilities to provide comprehensive word definitions. Built with modern Android development practices, it features a Material Design 3 interface with smooth animations and an intuitive user experience.

## Features

### Features
- Offline-first architecture with automatic database download on first launch
- Built-in English-Chinese bilingual dictionary (ECDICT, 3.77M+ entries)
- No internet connection required for basic lookups (after initial download)
- Real-time search suggestions and auto-completion
- Fast query response with millisecond-level performance

### Online Enhancement
- Supplementary detailed definitions via online API
- Intelligent local caching with 7-day validity period
- Graceful degradation to cached data when network fails
- Clear data source indicators (online/offline)

### User Experience
- Material Design 3 Expressive design language
- Smooth animations and transitions
- Dark mode support
- Dynamic color system
- Clean card-based layout
- One-tap search clearing

## Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Design System**: Material Design 3 (Expressive)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Minimum SDK**: Android 8.0 (API 26)
- **Dependency Injection**: Service Locator pattern
- **Database**: SQLite + Room
- **Networking**: Retrofit + OkHttp
- **Async Processing**: Kotlin Coroutines

## Architecture

The application follows the MVVM architecture pattern:

```
UI Layer (Compose) → ViewModel → Repository → Data Layer
                                              ├─ Offline DB (SQLite)
                                              ├─ Room Cache
                                              └─ Retrofit API
```

### Data Flow

```
User Query → ViewModel → Repository
                          ├─ 1. Offline Dictionary (Primary)
                          ├─ 2. Room Cache (Secondary)
                          ├─ 3. Online API (Fallback)
                          └─ 4. Expired Cache (Last Resort)
```

## Data Sources

### Offline Dictionary (Primary)
- **ECDICT** - Free English-Chinese dictionary database
- Repository: https://github.com/skywind3000/ECDICT
- License: MIT
- Entries: 3.77M+

### Online API (Secondary)
- [Free Dictionary API](https://dictionaryapi.dev/) - Free online dictionary service

## Build Instructions

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK (API 26+)

### Step 1: Clone the Repository

```bash
git clone https://github.com/ChidcGithub/HyperDict.git
cd HyperDict
```

### Step 2: Build and Run

#### Using Android Studio
1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Run on emulator or physical device
4. On first launch, the app will download the dictionary database automatically (~300MB)

#### Using Command Line

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease
```

## CI/CD

The project uses GitHub Actions for automated building and releasing:

- Automatic build of Debug and Release APKs
- Automatic release creation when tags are pushed
- APK upload to GitHub Releases

### Creating a New Release

```bash
git tag -a v0.0.1 -m "Release version 0.0.1"
git push origin v0.0.1
```

## Project Structure

```
app/src/main/java/com/hyperdict/app/
├── data/
│   ├── local/          # SQLite offline dictionary + Room cache
│   ├── remote/         # Retrofit API interfaces
│   ├── repository/     # Data repository (offline-first strategy)
│   └── model/          # Data models
├── ui/
│   ├── screens/        # Compose UI screens
│   ├── theme/          # Material Design 3 theme
│   └── viewmodel/      # ViewModel business logic
├── di/                 # Dependency injection (Service Locator)
└── MainActivity.kt     # Application entry point
```

## Roadmap

- Favorites/Bookmarks functionality
- Search history
- Text-to-speech pronunciation
- Additional dictionary sources
- Flashcard learning mode
- Import/Export word lists
- Home screen widget
- Share functionality

## Contributing

Issues and Pull Requests are welcome.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [ECDICT](https://github.com/skywind3000/ECDICT) - Offline dictionary data (3.77M+ entries)
  - Database source: [ecdict-sqlite release](https://github.com/skywind3000/ECDICT/releases/download/1.0.28/ecdict-sqlite-28.zip)
  - Author: [skywind3000](https://github.com/skywind3000)
  - License: MIT
- [Free Dictionary API](https://dictionaryapi.dev/) - Online dictionary API
- [Material Design 3](https://m3.material.io/) - Design guidelines

---

<div align="center">

Developed by [ChidcGithub](https://github.com/ChidcGithub)

</div>
