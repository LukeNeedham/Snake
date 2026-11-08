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
  - The score stays (mice eaten), and the bar is shown on the screen.
- [x] **Sprint mode.**
  - The player can boost the snake to go faster by holding the sprint corner (bottom left, or right in the settings): it sprints for as long as the finger
    is held there, and stops as soon as it lets go.
  - Sprinting drains the hunger bar faster.
  - A finger in the sprint corner never turns the snake; a tap anywhere else does. So one thumb sprints while the other turns.
- [x] **Birds.** Done: `BirdBody` (with `BirdFlight` and `BirdPerch`) is a critter that flies in from off the screen, lands in the oval
  (`BirdLanding` never picks a spot in front of the snake's mouth, and picks another one if the snake comes close while the bird is
  still coming down), hops and looks around, and flies off the moment the snake comes closer than the bird's alarm distance. It
  crouches for 0.3 seconds first, which is the only moment to catch it (a sprint lunge does it); it cannot be eaten in the air. Eating
  one scores 2 and fills a little more of the hunger bar than a mouse, but the point of it is its power up.
- [x] **Several kinds of birds, one power up each.** Small ones (blue tit, robin, sparrow) and large ones (blackbird, crow, pigeon), in
  `BirdKind`, each with its own sprites and colours (`BirdArt`, `BirdPalette`). The small ones are the most skittish.

## Power ups

- [ ] **Bunnies and ducks as real critters.** They were disguises of the vague critters, which are weird monsters now. Their
  sprites are in the git history (`SpriteArt.Bunny` and `SpriteArt.Chick`).
- [x] **Power-up critters.** The birds are the power ups (`PowerUp`, `PowerUpTracker`, `PowerUpBoosts`, numbers in `PowerUpTuning`):
  - **Zoom** (blue tit): the snake moves faster.
  - **Sprinter** (robin): sprinting is faster and costs less hunger.
  - **Sneaky** (sparrow): mice and birds only notice the snake from closer.
  - **Big mouth** (blackbird): the snake eats from further away.
  - **Toad-free** (crow): toads hop away from the snake.
  - **Mouse lure** (pigeon): more mice come, and faster. They do not come towards the snake yet.
- [x] **Power ups and downs stack.** They run at the same time, together with the effects of the toads (`Effect`), and each one
  runs out on its own. The screen shows all that is active: one bar for each, and a banner for the last one eaten.
- [ ] **Power-up polish.** Show the bigger mouth on the snake's head, and let the mice come towards the snake with the pheromones.

## Adventure mode

- [ ] **The razor.** One of the power ups gives the snake an electric razor that it holds in its mouth. While it has the razor
  it cannot eat anything, but it can cut the grass around the oval.
- [ ] **Levels and the map.** Cutting the grass lets the snake reach the edge of the screen; it then moves on to a different
  level. A map shows how the levels fit together, side by side.

## Later: a roguelike

Adventure mode is meant to grow into a roguelike:

- [ ] **Shops** in some levels, where the snake buys power ups and more.
- [ ] **Bosses** in some levels. The snake has to dodge falling traps and the like, while eating mice to become long enough to
  wrap the boss and constrict it to kill it. A boss is too big to fit in the snake's mouth, so it cannot be eaten.
