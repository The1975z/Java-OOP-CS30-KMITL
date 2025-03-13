# Clamp War - Java OOP Board Game

## Introduction
Clamp War is a strategic board game implemented in Java with Object-Oriented Programming (OOP) principles. The game is played on an 8x8 board, where players aim to capture and eliminate the opponent's pieces using strategic moves. The game incorporates unique capturing mechanics such as clamping and inserting.

## Rules and Mechanics
- **Movement:** Pieces move in straight lines (up, down, left, right), similar to a rook in chess.
- **Capturing Methods:**
  - **Clamping:** A piece is captured when it is surrounded on two opposite sides (left-right or top-bottom).
  - **Special Clamping:** A piece located at a corner can be captured under specific conditions.
  - **Insertion:** A piece placed between two opponent pieces can capture them.
- **Winning Conditions:**
  - A player wins by capturing more pieces before the time runs out.
  - A player wins immediately if the opponent has only one piece left.

## Project Structure
- Updated_tree.txt

## Game Flow
1. **Application Start:**
   - `Main.java` initializes the game and launches `StartScreen`.
   - `StartScreen` displays buttons for "Play Game," "Rules," and "Exit".
2. **Game Initialization:**
   - Clicking "Play Game" starts `GameFrame`.
   - `GameFrame` sets up the board, game controller, timer, and UI panels.
3. **Gameplay Mechanics:**
   - Players move pieces using mouse clicks.
   - `BoardLogic` checks for valid moves and captures.
   - Capturing is handled using the clamping and insertion mechanics.
4. **Turn Management:**
   - `GameController` switches turns after each move.
   - `TimerManager` tracks player time limits.
5. **Game End Conditions:**
   - The game ends when one player has only one piece left.
   - The player with more captured pieces when time expires wins.

## Controls and Features
- **Mouse Input:**
  - Click a piece to select it.
  - Click a destination tile to move.
- **Game UI:**
  - Timer and piece count displayed in `InfoPanel`.
  - Move history tracked in `MoveHistoryPanel`.
- **Customizable Window:**
  - `TitleBar` allows resizing, minimizing, and closing the game.

## Installation and Setup
### Prerequisites
- Java 8 or later
- A Java IDE (IntelliJ IDEA, Eclipse, or VS Code)

### Clone Repository
```sh
git clone https://github.com/The1975z/Java-OOP-CS30-KMITL.git
cd Java-OOP-CS30-KMITL
```

### Run the Game
1. Open the project in your Java IDE.
2. Compile and run `Main.java`.
3. Enjoy the game!

## Contributors
- **Project Author:** The1975z And CS30 GANG KMITL
- **Contributors:** Open for community contributions

## License
This project is licensed under the MIT License - see the LICENSE file for details.

