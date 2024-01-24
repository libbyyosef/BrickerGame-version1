# BrickerGame-version1
**Overview**
BrickerGame Version 1 is a simple console-based game written in Java. The game involves breaking bricks with a ball, and the player can control the paddle to prevent the ball from falling.

**Table of Contents**

- Features
- Getting Started
- Game Rules
- Classes
- CollisionStrategy
- BrickerGameManager
- Ball
- Brick
- GraphicLifeCounter
- NumericLifeCounter
- Paddle
  
**Features**
- Console-based gameplay.
- Control the paddle to prevent the ball from falling.
- Break bricks with the ball.
- Score tracking.

**Game Rules**

The game follows these basic rules:
- The player controls a paddle to prevent the ball from falling.
- Breaking bricks with the ball earns points.
- The player has a limited number of lives.
- The game ends when the player runs out of lives or successfully breaks all the bricks.

**Classes**

**CollisionStrategy**

Handles collisions between game objects, specifically the collision of bricks with the ball.

**BrickerGameManager**

Manages the overall game logic, including initializing game objects, handling user input, updating game state, and checking for game over conditions.

**Ball**

Represents the ball in the game, including its movement, collisions, and the sound played upon collision.

**Brick**

Represents a brick in the game, with a specific collision strategy to handle interactions with the ball.

**GraphicLifeCounter**

Displays the player's remaining lives as graphical hearts on the game board.

**NumericLifeCounter**

Displays the numeric representation of the player's remaining lives on the game board.

**Paddle**

Represents the player-controlled paddle, handling user input to move left or right within the game borders.

Such a fun game! Try to play and beat your high score! 🎮🚀


Powered by DanoGameLab, a library crafted by Dan Nirel.
