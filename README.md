<!-- Generated from readme.yml by LukeNeedham/ci-workflows (docs/readme.md). Edit readme.yml, not this file. -->
<p align="center">
  <img src="app-icon.png" width="128" alt="Snake icon">
</p>

# Snake

Snake with a twist: one button, a snake that only ever turns, and poisonous toads.

## Description

Snake is a one-button arcade game in pixel art. The snake never goes straight: it circles around itself, and the only
thing you can do is tap to change the direction of the turn. Steer it by twisting out of its own spiral, bounce it off the
edge of the oval, and eat the mice that run out of the tall grass. Each mouse makes the snake one section longer, with
a skin pattern that repeats as it grows. The snake can cross its own body without harm: the only things that kill it are
hunger and hay fever.

Beware the toads that hop out of the grass: each colour is poisonous in its own way. One makes the whole screen wavy,
one turns every animal into a weird monster, one makes the snake so dizzy that it ignores your taps for a few seconds, and
the grey one gives the snake hay fever: for a few seconds, touching the grass is deadly.

Birds fly in from off the screen and land in the oval: blue tits, robins and sparrows, blackbirds, crows and pigeons. Each kind is
worth a little more than a mouse and gives the snake a different power up for a few seconds: it goes faster, sprints for less, is
sneakier, eats from further away, blows up every toad and keeps new ones away, or carries a piece of cheese that brings mice
running from all over (but with its mouth full, the snake cannot eat until the cheese is gone). Power ups stack with each other
and with the toads' poison. Birds are rare, only one at a time, and extremely skittish: they fly off the moment the snake gets
close, so catching one takes a sprint.

The snake is always hungry: a bar at the top of the screen runs down, a mouse fills it, a toad hardly does, and the
snake starves when it is empty. Hold the corner at the bottom left to sprint (you can move it to the right in the settings), which is faster but burns
the bar quicker, while your other thumb taps anywhere else to turn. The button at the top left pauses the game and opens a
menu with Resume, Restart and Settings.
The mice run from a snake they see coming, but they cannot see what is behind them.

## Screenshots

<img src="docs/screenshots/01-GamePageReady.png" width="240" alt="Game Page Ready">
<img src="docs/screenshots/02-GamePage.png" width="240" alt="Game Page">
<img src="docs/screenshots/03-GamePageBirds.png" width="240" alt="Game Page Birds">
<img src="docs/screenshots/04-GamePageHallucinating.png" width="240" alt="Game Page Hallucinating">
<img src="docs/screenshots/05-GamePagePaused.png" width="240" alt="Game Page Paused">
<img src="docs/screenshots/06-GamePageGameOver.png" width="240" alt="Game Page Game Over">

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
