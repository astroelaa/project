# Target Hunter - Java Arcade Game

A JavaFX-based arcade game with player registration, different difficulty levels, and persistent player statistics.

## Features

- **Player Registration and Authentication**: Create an account and log in to track your progress
- **Multiple Difficulty Levels**: Choose from Easy, Medium, or Hard difficulties
- **Target Shooting Gameplay**: Click moving targets to score points
- **Player Profiles**: View your game history and statistics
- **Leaderboards**: Compete with other players for the highest scores
- **SQLite Database**: All player data and game sessions are saved persistently

## Game Instructions

1. **Register/Login**: Create an account or log in with existing credentials
2. **Choose Difficulty**:
   - **Easy**: Larger targets, slower movement, 30 seconds, 20 targets
   - **Medium**: Medium targets, moderate speed, 45 seconds, 30 targets
   - **Hard**: Small targets, fast movement, 60 seconds, 40 targets
3. **Gameplay**: Click on the targets as they appear on screen
4. **Scoring**: Points vary by difficulty (Easy: 10, Medium: 20, Hard: 30)
5. **Game End**: Game ends when time runs out or all targets are hit
6. **Stats**: View your performance in the profile section

## Running the Game

1. Make sure you have Java JDK 11+ installed
2. Clone this repository
3. Build and run using Gradle:
   ```
   ./gradlew run
   ```

## Technical Details

- Built with JavaFX for the UI
- Uses SQLite for persistent data storage
- Implements MVC architecture
- Features smooth animations and visual effects

## Database Schema

- **Players Table**: Stores user accounts
- **Game Sessions Table**: Records gameplay history

## Dependencies

- Java JDK 11+
- JavaFX 17
- SQLite JDBC Driver