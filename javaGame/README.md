# AI Shooter Challenge

A Java 17 Swing implementation of the PDF challenge in this folder.

## Features

- Player airplane movement with WASD or arrow keys.
- Spacebar shooting.
- Enemy aircraft use BFS pathfinding on a grid map.
- Asteroids block movement and force enemies to find another path.
- Score, HP, level, pause, game over, and restart.
- No external libraries are required.

## Run

Requires Java 17 JDK or newer. Make sure `java` and `javac` are available in PATH.

On Windows:

```bat
run.bat
```

Or compile manually:

```bat
dir /s /b src\main\java\*.java > sources.txt
javac -encoding UTF-8 -d out @sources.txt
java -cp out aishooter.GameFrame
```

To compile without opening the game window:

```bat
run.bat compile
```

## Controls

- `WASD` or arrow keys: move
- `Space`: shoot
- `P`: pause or resume
- `R`: restart

## Main Classes

- `GameFrame`: main window
- `GamePanel`: game loop, input, rendering
- `Player`: player airplane
- `Enemy`: enemy airplane with BFS chasing
- `Bullet`: player projectile
- `GameMap`: grid map and obstacles
- `Node`: BFS node
- `ScoreBoard`: score, HP, level, controls
- `CollisionManager`: bullet, enemy, and player collisions
