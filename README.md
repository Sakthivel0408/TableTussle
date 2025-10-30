# TableTussle - Tic Tac Toe Game

A feature-rich Tic Tac Toe Android application with online multiplayer, AI opponents, statistics tracking, and complete audio experience.

## 🎮 Features

### Game Modes
- **Single Player** - Play against AI with adjustable difficulty (Easy/Medium/Hard)
- **Online Multiplayer** - Create or join rooms to play with friends
- **Quick Match** - Fast random online matchmaking

### Audio System
- **Sound Effects** (80% volume)
  - Button clicks
  - Game moves
  - Win/Lose/Draw notifications
- **Background Music** (40% volume)
  - Smooth, looping melody
  - Auto pause/resume on app lifecycle
  - Toggleable in settings

### User Features
- **Statistics Tracking** - Win/loss records, win rate, streaks
- **QR Code Sharing** - Generate and scan QR codes for room joining
- **Dark/Light Theme** - Complete theme support with smooth transitions
- **Settings** - Control sound effects, background music, and theme

## 🚀 Getting Started

### Prerequisites
- Android Studio (latest version recommended)
- Android SDK (minimum API 24)
- Java JDK 11 or higher
- Android device or emulator for testing

### Build & Install

1. **Clone the repository** (or open the project in Android Studio)

2. **Build the project:**
   ```bash
   cd /home/sakthivel-a/StudioProjects/TableTussle
   ./gradlew clean assembleDebug
   ```

3. **Install on device:**
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

4. **Launch the app:**
   ```bash
   adb shell am start -n com.example.tabletussle/.LoginActivity
   ```

### Quick Build Commands

```bash
# Clean build
./gradlew clean

# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Install and run
./gradlew installDebug
adb shell am start -n com.example.tabletussle/.LoginActivity
```

## 📱 How to Use

### First Time Setup
1. Launch the app
2. Sign up with username and password
3. Choose your game mode
4. Start playing!

### Single Player
1. Click "Play Now"
2. Select AI difficulty (Easy/Medium/Hard)
3. Make your move - you're X, AI is O
4. Try to get three in a row!

### Online Multiplayer
**Create Room:**
1. Click "Create Room"
2. Enter room name
3. Share room code or QR code with friend
4. Wait for opponent to join

**Join Room:**
1. Click "Join Room"
2. Enter room code or scan QR code
3. Wait for game to start

### Quick Match
1. Click "Quick Match"
2. Wait for matchmaking
3. Game starts automatically when opponent found

## 🎵 Audio Features

### Background Music
- Custom background music file: `keys_of_moon_the_success.mp3`
- Located in: `app/src/main/res/raw/`
- Loops continuously during gameplay
- Volume: 40% of maximum
- Can be toggled ON/OFF in Settings

### Changing Background Music
To replace the background music:

1. Find your MP3 file
2. Rename it to follow Android resource naming rules:
   - Only lowercase letters (a-z)
   - Numbers (0-9)
   - Underscores (_)
   - No spaces, hyphens, or special characters
   - Example: `my_custom_music.mp3`

3. Place the file in: `app/src/main/res/raw/`

4. Update `SoundManager.java`:
   ```java
   // Find this line (around line 120):
   backgroundMusic = MediaPlayer.create(context, R.raw.keys_of_moon_the_success);
   
   // Change to your file:
   backgroundMusic = MediaPlayer.create(context, R.raw.my_custom_music);
   ```

5. Rebuild the app

### Sound Effects
All sound effects use ToneGenerator for consistent, lightweight audio:
- **Click:** Short beep for button presses
- **Move:** Acknowledgment tone for game moves
- **Win:** Victory fanfare
- **Lose:** Defeat tone
- **Draw:** Neutral tone

## 📊 Statistics

Track your progress with comprehensive statistics:
- **Games Played** - Total number of games
- **Games Won** - Number of victories
- **Win Rate** - Percentage of games won
- **Current Streak** - Consecutive wins
- **Best Streak** - Longest winning streak

Statistics are stored locally using SQLite database and persist across app sessions.

## ⚙️ Settings

Access settings from the main menu:
- **Sound Effects** - Toggle game sound effects ON/OFF
- **Background Music** - Toggle background music ON/OFF
- **Theme** - Switch between Dark and Light mode

All settings are saved automatically and persist across app restarts.

## 🎨 Themes

### Light Theme
- Clean, bright interface
- Easy to read in daylight
- Traditional color scheme

### Dark Theme
- Easy on the eyes in low light
- AMOLED-friendly dark backgrounds
- Reduced eye strain

The app automatically applies the selected theme across all screens.

## 🔧 Troubleshooting

### Build Errors

**Problem:** Resource naming errors
```
Error: '-' is not a valid file-based resource name character
```
**Solution:** Rename resource files to use only lowercase a-z, 0-9, or underscore.

**Problem:** XML parsing errors
**Solution:** Check XML files for unclosed tags or malformed structure.

### Audio Issues

**Problem:** No background music
**Solutions:**
1. Check Settings → Background Music is ON
2. Increase device media volume
3. Disable silent/vibrate mode
4. Restart the app

**Problem:** Music file not found
**Solution:** Ensure the MP3 file is in `app/src/main/res/raw/` with valid filename.

### Game Issues

**Problem:** App crashes on "Play Now"
**Solutions:**
1. Check logcat for errors: `adb logcat | grep TableTussle`
2. Clear app data and restart
3. Reinstall the app

**Problem:** Statistics not updating
**Solutions:**
1. Play a complete game (don't quit mid-game)
2. Check database permissions
3. Clear app data and replay

## 📁 Project Structure

```
TableTussle/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/tabletussle/
│   │   │   │   ├── LoginActivity.java
│   │   │   │   ├── MainActivity.java
│   │   │   │   ├── GameActivity.java
│   │   │   │   ├── CreateRoomActivity.java
│   │   │   │   ├── JoinRoomActivity.java
│   │   │   │   ├── QuickMatchActivity.java
│   │   │   │   ├── StatisticsActivity.java
│   │   │   │   ├── SettingsActivity.java
│   │   │   │   ├── SoundManager.java
│   │   │   │   ├── ThemeManager.java
│   │   │   │   └── DatabaseHelper.java
│   │   │   ├── res/
│   │   │   │   ├── layout/ - UI layouts
│   │   │   │   ├── values/ - Strings, colors, themes
│   │   │   │   ├── values-night/ - Dark theme resources
│   │   │   │   ├── drawable/ - Icons and graphics
│   │   │   │   └── raw/ - Audio files
│   │   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
└── README.md
```

## 🛠️ Technical Details

### Technologies Used
- **Language:** Java
- **Database:** SQLite
- **Audio:** MediaPlayer (music), ToneGenerator (SFX)
- **QR Code:** ZXing library
- **Network:** Firebase Realtime Database (for multiplayer)
- **UI:** Material Design components

### Minimum Requirements
- Android 7.0 (API 24) or higher
- 50 MB free storage
- Internet connection (for multiplayer modes)

### Permissions Required
- `INTERNET` - For online multiplayer
- `CAMERA` - For QR code scanning
- `VIBRATE` - For haptic feedback

## 🐛 Known Issues

- None currently reported

## 📝 Development Notes

### AI Difficulty Levels
- **Easy:** Random moves with occasional mistakes
- **Medium:** Basic strategy with some planning
- **Hard:** Minimax algorithm, nearly unbeatable

### Database Schema
```sql
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE,
    password TEXT
);

CREATE TABLE statistics (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT,
    games_played INTEGER,
    games_won INTEGER,
    win_rate REAL,
    current_streak INTEGER,
    best_streak INTEGER
);
```

## 📄 License

This project is created for educational purposes.

## 👤 Author

**Sakthivel0408**
- Project: TableTussle
- Date: October 2025

## 🙏 Acknowledgments

- ZXing library for QR code functionality
- Material Design for UI components
- Background music: "Keys of Moon - The Success" by Chosic

## 📞 Support

For issues or questions:
1. Check the Troubleshooting section above
2. Review logcat output: `adb logcat | grep TableTussle`
3. Clean and rebuild the project

---

**Enjoy playing TableTussle! 🎮**

