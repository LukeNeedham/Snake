<!-- Generated from readme.yml by LukeNeedham/ci-workflows (docs/readme.md). Edit readme.yml, not this file. -->
<p align="center">
  <img src="app-icon.png" width="128" alt="Snake icon">
</p>

# Snake

The classic snake game, played with swipes.

## Description

Snake is the arcade classic: guide a growing snake around the board, eat the food and avoid the walls and your own tail.
Swipe anywhere to steer, tap to pause, and watch it speed up with every bite. The game pauses by itself when you leave the app,
and the app remembers your best score of the session.

## Screenshots

<img src="docs/screenshots/01-GamePage.png" width="240" alt="Game Page">
<img src="docs/screenshots/02-GamePageGameOver.png" width="240" alt="Game Page Game Over">
<img src="docs/screenshots/03-SettingsPage.png" width="240" alt="Settings Page">

## Getting started as a user

1. Install the latest build from the [AppHub](https://www.lukeneedham.com/apphub) app, or download the APK from the [Releases](https://github.com/LukeNeedham/Snake/releases) page.

Requires Android 6.0 (API 23) or newer.

## Getting started as a developer

Requirements: JDK 17 and the Android SDK (platform 36), for example through Android Studio.

```
git clone https://github.com/LukeNeedham/Snake.git
cd Snake
./gradlew assembleDebug    # the APK is in app/build/outputs/apk/debug/
./gradlew test             # unit tests
```

Every pull request is built by CI, which comments on it with a link to install the APK.

Like all my apps, it follows the shared architecture and conventions (single activity, Jetpack Compose, Navigation 3, MVVM with every page split into a `Page` and a `PageContent`, Koin): see [ci-workflows](https://github.com/LukeNeedham/ci-workflows/blob/main/docs/new-app.md).

## Contributing

Branch from `main`, make the change, and open a pull request. CI must be green before it is merged.

## License

All rights reserved. See [LICENSE](LICENSE).
