# hatvdash

This project is an Android TV dashboard for Home Assistant. Before building, you must provide local configuration files that are excluded from source control.

## Configuration

1. Copy `app/src/main/java/com/matthewbennin/hatvdash/network/HomeAssistantConfig.kt.template` to `app/src/main/java/com/matthewbennin/hatvdash/network/HomeAssistantConfig.kt`.
2. Edit the new file and set `BASE_URL` to your Home Assistant base URL and `TOKEN` to a long-lived access token.
3. Copy `app/src/main/res/xml/network_security_config_template.xml` to `app/src/main/res/xml/network_security_config.xml`.
4. Replace `YOUR_HOME_ASSISTANT_IP_ADDRESS` in the copied XML with the host name or IP address of your Home Assistant instance.

## Building and Running

Run the following commands with the Gradle wrapper:

```bash
./gradlew assembleDebug   # Build the debug APK
./gradlew installDebug    # Install on a connected device or emulator
```

You can also open the project in Android Studio and use the normal run/debug actions.

## Features

- Connects to Home Assistant using a WebSocket for real time updates
- Parses Lovelace configuration and lists available dashboards
- Dynamic rendering of several card types (see below)
- Focus friendly UI designed for Android TV remote input
- "More info" popups for lights, input booleans, buttons, numbers and selects
- Weather forecast retrieval for weather entities

## Architecture Overview

- **MainActivity** – entry point that sets up the Compose theme and launches `DashboardEntryPoint`
- **network** – WebSocket connection (`HaWebSocketManager`) and configuration (`HomeAssistantConfig`)
- **data** – `DashboardRepository` loads Lovelace dashboards; `EntityStateManager` caches entity states and weather forecasts
- **ui** – Compose UI components
  - `DashboardEntryPoint` handles top level navigation
  - `launchscreen` shows the list of dashboards
  - `dashboard` renders individual views using `SiftJson` and `SectionsRenderer`
  - `cards` contains implementations of supported Lovelace cards
  - `infoCards` provide the more‑info popups
- **logic** – input handlers such as `RemotePressHandler` and `InteractionHandler`
- **utils** – helpers like `CardRouter` and `SiftJson`

## Supported Lovelace Cards

- `button`
- `entity`
- `vertical-stack`
- `horizontal-stack`
- `grid`
- `weather-forecast`

## Roadmap

- Support additional Lovelace view types (masonry, sidebar, panel)
- Implement more card types and service call actions
- Replace placeholder interaction handlers with real Home Assistant service calls
- Flesh out the settings screen
- General polish and bug fixes


## License

This project is licensed under the [MIT License](LICENSE).
