# Todo

Improvements that are planned but **not started**. Tick an item when it is done, and move the design notes of a bigger item
into its own doc when work on it starts.

## Next up

- [x] **Mice run from the snake.** Done in `MouseGait`: a roaming mouse that sees the snake approaching runs away. It does not notice a snake that approaches from behind: the mouse only
  sees what is in front of it (its `facing`), so a snake that sneaks up on a mouse's back can still eat it.
- [x] **New toad sprites.** The current toads look like slimes. Draw new ones that are obviously frogs (eyes on top of the head,
  long back legs, a pale belly), still tinted per kind: purple, teal and pink. Check them with a rendered preview, like
  the sprites in `Sprites.kt`.
- [x] **Fewer mice.** Mice spawn too often: lower the spawn rate and the number of mice at a time (`spawnCritters` in
  `GameWorld`, and the timers in `GameTuning`).
- [x] **A hunger bar instead of just points.**
  - The snake has a hunger bar that goes down at a steady rate.
  - A mouse fills it; a toad fills it too, but only a tiny bit.
  - When it reaches zero the snake dies. That is the reason to keep eating.
  - The score stays (a mouse is 100 points, a bird 150, a toad 0), and the hunger is shown as a stomach in the top right corner that fills from the bottom.
- [x] **Sprint mode.**
  - The player can boost the snake to go faster by holding the sprint corner (bottom left, or right in the settings): it sprints for as long as the finger
    is held there, and stops as soon as it lets go.
  - Sprinting drains the hunger bar faster.
  - A finger in the sprint corner never turns the snake; a tap anywhere else does. So one thumb sprints while the other turns.
- [x] **Birds.** Done: `BirdBody` (with `BirdFlight` and `BirdPerch`) is a critter that flies in from off the screen, lands in the oval
  (`BirdLanding` never picks a spot in front of the snake's mouth, and picks another one if the snake comes close while the bird is
  still coming down), hops and looks around, and flies off the moment the snake comes closer than the bird's alarm distance. It
  crouches for 0.3 seconds first, which is the only moment to catch it (a sprint lunge does it); it cannot be eaten in the air. Eating
  one scores 150 and fills a little more of the hunger bar than a mouse, but the point of it is its power up.
- [x] **Several kinds of birds, one power up each.** Small ones (blue tit, robin, sparrow) and large ones (blackbird, crow, pigeon), in
  `BirdKind`, each with its own sprites and colours (`BirdArt`, `BirdPalette`). The small ones are the most skittish.

- [x] **Fewer, rarer birds.** Done: one bird at a time, the first once the score reaches 200 (and 30 seconds have passed), then one every 40 to 70 seconds.
  Original brief: There are too many birds. At most one bird on the screen at a time (drop `SpawnTuning.mostBirds` and
  `pointsPerExtraBird`, so the score never brings a second one), and a lot rarer: much longer waits between birds
  (`firstBirdDelay`, `birdDelayMin` and `birdDelayMax` in `SpawnTuning`). Update the tests in `CritterSpawnerTest` that count two birds.
- [x] **The snake does not die from touching itself, except under the cannibal effect.** Done: it comes from the brown toad
  (`ToadKind.Brown`, `Effect.Cannibal`, 12 seconds, "Cannibal!"; the grey toad now gives hayfever, which kills on touching the grass), shown with a bar and a banner like the other effects. The body is drawn
  in stretches so that where it crosses itself the part nearer the head lies over the other with a dark edge (`SnakeRenderer`).
  Original brief: Normally the snake can loop over its own body
  and nothing happens (so `snake.hitsItself()` no longer ends the game in `GameWorld.updatePlaying`): the only way to die is to starve.
  The **lupus curse** is a special curse that, while it is active, makes the snake die if it touches itself, as it does now. Decide how a
  snake catches the curse (an item, a critter or a boss?) and how long it lasts, then show it on the screen like the other effects and
  power ups. Until it exists, nothing kills the snake but hunger. Check how the snake is drawn when its body crosses itself.

- [x] **Intro story and tutorial.** Done: the map (`WorldLayout`) is the stage (the oval) at the top, a path of bare ground through the grass one screen
  long that leads down to the snake's house (a misshapen floor of raw stone with pebbles, ringed with rocks), and, to the left of the house,
  a wide path that leads to the room of the tutorial (an oval of sand, like the stage, on the screen to the left). The snake can go anywhere
  on the map (a wall bounce turns it the other way, like a tap), and the camera follows it down the path, and goes from the house to the room
  and back. `GamePhase.Intro` plays `IntroDirector`'s stages.
  - The story, in the house: the snake sleeps with its eyes shut and Zs until the player taps to wake it, it wakes up and blinks, sticks its
    tongue out, its stomach rumbles (zigzag lines along the body), three thought bubbles end in a mouse, the bubble pops and the snake starts to
    circle clockwise, "Tap to turn" (a tap turns it, taps before that do nothing). In the first game (`Opening.Tutorial`) a line of stones in the wall of the house closes
    the path up (`GateBounds`), and an arrow points to the path to the left.
  - The lessons, in the room, one after the other: the hunger bar shows for the first time, full, with an arrow and "Belly decreases over time",
    and runs down by two steps; the sprint button shows with an arrow and "Hold to zoom" until the corner has been held for 2 seconds, and
    the bar runs down while it does ("Zooming makes you hungry"); "Eat to not starve" and a mouse runs in from the grass to the middle of the
    room to be eaten; "You can also eat toads" and the pink (dizzy) toad hops in, and has to be eaten, "But they poison you", and its
    dizziness has to run out; "Try to catch birds" and a bird flies in and has to be eaten, "They give you powers". The animals are tame
    (`tameTarget`, `isTame`): they go to the middle and wait, and do not run, hop or fly off. Nothing starves in the lessons. Then an arrow
    shows the way back to the house; there the stones are gone and an arrow points up the path to the stage.
  - Afterwards: the player has done the tutorial (`SettingsStore.isTutorialDone`), and every game opens in `Opening.Returning`: the snake wakes
    in its house, the path up is open, and no lessons are told. A restart is `Opening.Oval`. The room of the tutorial is the way to do the
    tutorial again: when the snake goes into it, the lessons start again from the sprint (a replay ends once the bird is eaten).

## Power ups

- [ ] **Bunnies and ducks as real critters, in the levels.** Not for now: they will come in different levels with different biomes
  (see "Adventure mode"). Their sprites are in the git history (`SpriteArt.Bunny` and `SpriteArt.Chick`).
- [x] **Power-up critters.** The birds are the power ups (`PowerUp`, `PowerUpTracker`, `PowerUpBoosts`, numbers in `PowerUpTuning`):
  - **Zoom** (blue tit): the snake moves faster.
  - **Sprinter** (robin): sprinting is faster and costs less hunger.
  - **Sneaky** (sparrow): mice and birds only notice the snake from closer.
  - **Big mouth** (blackbird): the snake eats from further away.
  - **Toad-free** (crow): every toad on the screen explodes, and no new toad comes while it lasts.
  - **Cheese** (pigeon): the snake carries cheese in its mouth, so it cannot eat; more mice come, and gather around it.
- [x] **Power ups and downs stack.** They run at the same time, together with the effects of the toads (`Effect`), and each one
  runs out on its own. The screen shows all that is active: one bar for each, and a banner for the last one eaten.
- [x] **Cheese.** Done: `PowerUp.Cheese` (10 seconds). The snake carries the cheese in its mouth (`CheeseRenderer`), and a snake that carries
  something cannot eat anything (`PowerUpBoosts.mouthIsFull`, `Feeding`): a rule for anything it will carry later. More mice come, and
  roaming mice smell it from far away, run to it and stand around it (`MouseGait`); for 1.5 seconds after the cheese is gone they are
  dazed instead of fleeing, so the snake can eat them. Original brief: The mouse power up (the pigeon's, `PowerUp.Pheromones`) is called **Cheese**, not "mouse lure": rename it everywhere
  (the enum entry, `PowerUpBoosts.attractsMice`, `power_up_pheromones`, the banner "Mouse lure!", the tests and `readme.yml`). While it is on,
  the snake carries a little piece of cheese in its mouth (drawn in `SnakeHeadRenderer`), and mice run **towards** the snake instead of
  away from it (a mouse that sees the snake no longer flees: it runs at it, in `MouseGait`, through the `SnakeAura`). Decide whether it
  also keeps bringing more mice, as the pheromones do now, and whether a mouse is eaten when it reaches the cheese.
- [x] **Toad-free explodes the toads.** Done (`ToadBlast`, `PowerUp.ToadFree`): the exploded toads feed and score nothing, and the
  toad timer waits while the power up is on. Original brief: Eating the crow (`PowerUp.ToadRepellent`, "Toad-free!") makes every toad on the screen explode at
  once (a burst of particles in each toad's colours with `ParticleSystem`, like the one when the snake dies, and the toads are removed
  from the world), and while the power up is active no new toads come out of the grass (`CritterSpawner` skips the toad timer, and a
  toad that is still coming in from the grass is blown up too). This replaces the toads hopping away from the snake: remove that
  (`SnakeAura.repelsToads`, the scared hop in `ToadGait`, `CritterTuning.repelDistance` and their tests). Decide whether the toads that
  explode feed the snake or score anything (probably not), and whether the toad timer carries on while it is off.
- [x] **Power-up polish.** Done: with Big mouth on, the snout is swollen and the mouth is a wide gape (`SnakeHeadRenderer`).

## Adventure mode

- [ ] **The razor.** One of the power ups gives the snake an electric razor that it holds in its mouth. While it has the razor
  it cannot eat anything, but it can cut the grass around the oval.
- [ ] **Levels and the map.** Cutting the grass lets the snake reach the edge of the screen; it then moves on to a different
  level. A map shows how the levels fit together, side by side.
  - Done so far: once the whole snake is in the oval, the way it came in by grows shut with grass (`WorldLayout.isStageSealed`, `SwitchBounds`, `SealPatch`), so
    the stage is a perfect oval and nothing gets in or out. It opens again when the stage is won: each stage will have win conditions of its own, and call
    `GameWorld.completeStage()` when they are met. Nothing calls it yet, so for now the snake cannot leave the oval (`DenTuning.sealsStage` turns it off).

## Later: a roguelike

Adventure mode is meant to grow into a roguelike:

- [ ] **Shops** in some levels, where the snake buys power ups and more.
- [ ] **Bosses** in some levels. The snake has to dodge falling traps and the like, while eating mice to become long enough to
  wrap the boss and constrict it to kill it. A boss is too big to fit in the snake's mouth, so it cannot be eaten.
