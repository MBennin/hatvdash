# AGENTS.md for HATVDash

Welcome! This file defines what Codex is allowed to do inside the HATVDash project and provides guidance on how to contribute safely and effectively.

## 🔐 Trusted Commands

At this stage, Codex is only allowed to run:

- `./gradlew test` – Run unit tests for JVM and Android-safe code

As development progresses, more commands will be allowed and added to this list.

## 📂 Files and Folders Codex Should Work In

Codex may read from and reference:

- Kotlin source code in:
    - `app/src/main/java/com/matthewbennin/hatvdash/`
    - `app/src/test/java/`
    - `build.gradle.kts`, `settings.gradle.kts`, and `local.properties`
    - `gradlew`, `gradle/**`

Codex may write to (output, generate, or modify):

- `build/` and `app/build/` – build artifacts
- `test-results/` – test output

It should not modify or generate files outside these areas unless explicitly approved.

## ✨ Style and Contribution Guidelines

- Stick to the project’s existing formatting conventions (Kotlin + Jetpack Compose)
- Keep functions concise and composables readable
- Prefer splitting logic into utility classes when possible
- Favor declarative UI — avoid legacy `View` code unless necessary
- Use `@Composable` annotations where appropriate

## 🚧 Code Migration and Work in Progress

The app is actively being migrated from View-based rendering to full Jetpack Compose. All new card components and layouts should be written in Compose.

Legacy files like `DashboardRenderer.kt` are being phased out and should not be extended unless the code is clearly labeled as temporary.

## 🧪 Validating Your Work

To validate changes, Codex should run:
```bash
./gradlew test
