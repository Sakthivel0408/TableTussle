# Table Tussle - SQLite Database Implementation

## Overview
The app now uses **SQLite database with Room** to store all user data persistently. No more SharedPreferences for user management!

## Database Structure

### Tables

#### `users` Table
| Column | Type | Description |
|--------|------|-------------|
| id | INTEGER | Primary key (auto-increment) |
| username | TEXT | User's display name |
| email | TEXT | User's email (unique) |
| password | TEXT | User's password |
| memberSince | TEXT | Registration month/year |
| gamesPlayed | INTEGER | Total games played |
| gamesWon | INTEGER | Total games won |
| totalScore | INTEGER | Cumulative score |
| createdAt | LONG | Registration timestamp |
| lastLoginAt | LONG | Last login timestamp |

## Key Components

### 1. **User.java** - Entity Class
Represents a user in the database with all their information.

### 2. **UserDao.java** - Data Access Object
Provides methods to interact with the database:
- `insertUser(User)` - Register a new user
- `login(email, password)` - Authenticate user
- `getUserById(id)` - Get user details
- `getUserByEmail(email)` - Check if email exists
- `incrementGamesPlayed(userId)` - Track games played
- `incrementGamesWon(userId)` - Track wins
- `addScore(userId, score)` - Update total score

### 3. **AppDatabase.java** - Database Instance
Singleton database instance using Room.

### 4. **UserSession.java** - Session Management
Manages logged-in user session:
- `createLoginSession(userId)` - Log in user
- `getUserId()` - Get current user ID
- `isLoggedIn()` - Check login status
- `logout()` - Clear session

### 5. **GameStatsManager.java** - Helper Class
Easy-to-use helper for updating game statistics.

## How to Use

### During Registration
```java
// Already implemented in RegisterActivity.java
User newUser = new User(username, email, password, memberSince);
long userId = userDao.insertUser(newUser);
userSession.createLoginSession((int) userId);
```

### During Login
```java
// Already implemented in LoginActivity.java
User user = userDao.login(email, password);
if (user != null) {
    userSession.createLoginSession(user.getId());
}
```

### Getting User Data
```java
// Already implemented in ProfileActivity and StatisticsActivity
int userId = userSession.getUserId();
User user = userDao.getUserById(userId);

// Access user data
String username = user.getUsername();
int gamesPlayed = user.getGamesPlayed();
```

### Recording Game Results
```java
// Use this in your game activity when a game ends
GameStatsManager statsManager = new GameStatsManager(this);

// When game ends
boolean playerWon = true; // or false
int scoreEarned = 50;
statsManager.recordGameResult(playerWon, scoreEarned);
```

### Manual Stats Update
```java
// Get database instance
AppDatabase database = AppDatabase.getInstance(this);
UserDao userDao = database.userDao();
UserSession session = new UserSession(this);

int userId = session.getUserId();

// Update stats
userDao.incrementGamesPlayed(userId);
userDao.incrementGamesWon(userId);
userDao.addScore(userId, 100);
```

## Updated Activities

### ✅ LoginActivity
- Uses SQLite to authenticate users
- Validates against database instead of SharedPreferences

### ✅ RegisterActivity
- Saves new users to SQLite database
- Checks for duplicate emails
- Auto-generates memberSince date

### ✅ ProfileActivity
- Loads user data from database
- Shows username, email, and member since date
- Logout clears user session

### ✅ StatisticsActivity
- Displays real-time stats from database
- Shows games played, won, win rate, and total score

## Database Location
The SQLite database is stored at:
```
/data/data/com.example.tabletussle/databases/table_tussle_database
```

## Example: Complete Game Flow

```java
// In your game activity
public class GameActivity extends AppCompatActivity {
    
    private GameStatsManager statsManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        
        statsManager = new GameStatsManager(this);
    }
    
    private void onGameEnd(boolean won, int score) {
        // Update database
        statsManager.recordGameResult(won, score);
        
        // Show results
        Toast.makeText(this, 
            won ? "You won!" : "Better luck next time!", 
            Toast.LENGTH_SHORT).show();
    }
}
```

## Benefits of SQLite

✅ **Persistent Storage** - Data survives app restarts  
✅ **Structured Data** - Organized tables with relationships  
✅ **Query Power** - Complex queries and filtering  
✅ **No Size Limit** - Store unlimited users and games  
✅ **Data Integrity** - ACID compliance ensures data safety  
✅ **Easy Backup** - Single database file to backup  

## Next Steps

1. **Add Game History Table** - Store individual game records
2. **Add Friends Table** - Implement friend system
3. **Add Achievements Table** - Track unlocked achievements
4. **Background Threads** - Move database operations off main thread
5. **Data Encryption** - Encrypt sensitive data like passwords

## Notes

⚠️ Currently using `.allowMainThreadQueries()` for simplicity. In production, use background threads with LiveData or Coroutines.

⚠️ Passwords are stored in plain text. Consider using password hashing (BCrypt) before production.

