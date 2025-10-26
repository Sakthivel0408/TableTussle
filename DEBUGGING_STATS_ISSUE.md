# 🔍 Debugging Guide: Games Won & Win Rate Showing Zero

## Problem
Games Played is updating correctly, but **Games Won** and **Win Rate** remain at 0 even after winning games.

---

## ✅ Fixes Applied

### 1. **StatisticsActivity - Data Refresh**
**Problem**: Data was only loaded on activity creation, not when returning from game.

**Fix Applied**:
```java
@Override
protected void onResume() {
    super.onResume();
    // Reload user data when activity resumes (e.g., returning from game)
    loadUserData();
}
```

**Benefit**: Statistics now refresh every time you open the Statistics screen.

### 2. **Thread Safety for Data Loading**
**Problem**: Database queries running on main thread.

**Fix Applied**:
```java
private void loadUserData() {
    new Thread(() -> {
        User user = userDao.getUserById(userId);
        runOnUiThread(() -> {
            // Update UI with fresh data
        });
    }).start();
}
```

**Benefit**: No UI blocking, fresh data from database.

### 3. **Enhanced Logging**
**Problem**: No visibility into whether database updates were working.

**Fix Applied**:
```java
android.util.Log.d("GameStats", "Incremented games won for user " + userId);
android.util.Log.d("GameStats", "Updated stats - Games: " + games + ", Wins: " + wins);
```

**Benefit**: Can now see exactly what's happening in Logcat.

---

## 🧪 Testing Steps

### Step 1: Clear App Data (Fresh Start)
```bash
# In Android Studio or via ADB:
Settings → Apps → TableTussle → Storage → Clear Data
```
This ensures you're testing with a clean slate.

### Step 2: Register New Account
1. Open app
2. Click "Register"
3. Create account: `test@test.com` / `password123`
4. Login successful

### Step 3: Play and WIN a Game
1. Click "Play Now"
2. **IMPORTANT**: You must WIN the game (beat the AI)
3. Watch for toast: "Stats updated: Win (+10 pts)"
4. Click "Play Again" if you want to play more rounds
5. When done, click Menu → Quit

### Step 4: Check Logcat
Open Logcat and filter by "GameStats":
```
GameStats: Incremented games played for user 1
GameStats: Incremented games won for user 1
GameStats: Added 10 points for user 1
GameStats: Updated stats - Games: 1, Wins: 1, Score: 10
```

### Step 5: Verify in Statistics
1. Go back to Main Menu
2. Click "Statistics"
3. Check the displayed values:
   - **Games Played**: Should be > 0
   - **Games Won**: Should match number of wins
   - **Win Rate**: Should be (Wins/Games) × 100%
   - **Total Score**: Should show accumulated points

---

## 🐛 Common Issues & Solutions

### Issue 1: "Games Won Still Shows 0"

**Possible Causes**:
1. ❌ You're playing but LOSING to the AI
2. ❌ You're getting DRAWS (no winner)
3. ❌ Database not updating due to guest mode

**Solutions**:
✅ **Make sure you WIN the game**
   - Get 3 X's in a row before AI gets 3 O's
   - Dialog should say "You Win! 🎉"

✅ **Check you're logged in**
   - Guest users don't save stats
   - Verify you see your username at top

✅ **Check Logcat for errors**
   - Filter by "GameStats"
   - Look for "Incremented games won" message

### Issue 2: "Statistics Don't Update Immediately"

**Cause**: Background thread delay

**Solution**:
✅ Wait 1-2 seconds after game ends
✅ Or close and reopen Statistics screen (triggers onResume)

### Issue 3: "All Stats Show 0"

**Cause**: Guest mode or not logged in

**Solution**:
✅ Logout and login again
✅ Register new account
✅ Check UserSession.getUserId() returns valid ID

---

## 📊 Database Verification

### Option 1: Using Android Studio Database Inspector
1. Run app on emulator/device
2. View → Tool Windows → App Inspection
3. Select "TableTussle" app
4. Click "Database Inspector"
5. Open "app-database"
6. View "users" table
7. Check values for gamesPlayed, gamesWon, totalScore

### Option 2: Using ADB Shell
```bash
# Access device shell
adb shell

# Navigate to app directory
cd /data/data/com.example.tabletussle/databases/

# Open database
sqlite3 app-database

# Query user stats
SELECT id, username, gamesPlayed, gamesWon, totalScore FROM users;

# Exit
.exit
```

---

## 🔬 Advanced Debugging

### Add Temporary Debug Button

Add this to GameActivity to manually test database:

```java
// In onCreate(), add a debug button
Button btnDebug = new Button(this);
btnDebug.setText("DEBUG: Test Stats");
btnDebug.setOnClickListener(v -> {
    int userId = userSession.getUserId();
    new Thread(() -> {
        userDao.incrementGamesPlayed(userId);
        userDao.incrementGamesWon(userId);
        userDao.addScore(userId, 10);
        
        User user = userDao.getUserById(userId);
        runOnUiThread(() -> {
            Toast.makeText(this, 
                "Games: " + user.getGamesPlayed() + 
                ", Wins: " + user.getGamesWon() + 
                ", Score: " + user.getTotalScore(), 
                Toast.LENGTH_LONG).show();
        });
    }).start();
});
```

Click the button and verify stats increase.

---

## ✅ Verification Checklist

Before reporting the issue isn't fixed, verify:

- [ ] You're logged in (not guest)
- [ ] You actually WON a game (not lost or draw)
- [ ] Toast notification appeared: "Stats updated: Win (+10 pts)"
- [ ] Logcat shows "Incremented games won" message
- [ ] You refreshed Statistics screen (closed and reopened it)
- [ ] You checked Database Inspector shows correct values
- [ ] You played multiple games to see if counter increases

---

## 📝 Expected Behavior

### After Playing 3 Games (2 Wins, 1 Loss):

**Statistics Screen Should Show**:
```
Games Played: 3
Games Won: 2
Win Rate: 66.7%
Total Score: 20
```

**Database Should Show**:
```sql
gamesPlayed: 3
gamesWon: 2
totalScore: 20
```

**Logcat Should Show**:
```
GameStats: Updated stats - Games: 1, Wins: 1, Score: 10
GameStats: Updated stats - Games: 2, Wins: 1, Score: 10  (after loss)
GameStats: Updated stats - Games: 3, Wins: 2, Score: 20
```

---

## 🆘 If Still Not Working

### Check These:

1. **Room Database Version**
   - If you updated the database schema, increment version number
   - Or clear app data to recreate database

2. **User ID Consistency**
   - Print userId in both GameActivity and StatisticsActivity
   - They should match

3. **Thread Race Condition**
   - Add `Thread.sleep(1000)` before loading stats
   - If this fixes it, there's a timing issue

4. **Database Corruption**
   - Uninstall and reinstall app
   - Test with fresh installation

---

## 🎯 Quick Test Script

1. Clear app data
2. Register: test@test.com / test123
3. Play Now
4. **Win 1 game** → Toast: "Stats updated: Win (+10 pts)"
5. Menu → Quit
6. Statistics → Should show: Games=1, Wins=1, Rate=100%, Score=10

**If this works**: System is functioning correctly!
**If this fails**: Check Logcat for errors

---

## 📱 Contact Support

If none of the above works, provide:
1. Logcat output (filtered by "GameStats")
2. Database Inspector screenshot
3. Statistics screen screenshot
4. Steps you followed
5. Android version and device model

---

## ✅ Summary

The fixes applied should resolve the issue. The most common cause is:
1. **Not actually winning games** (losing or drawing instead)
2. **Playing as guest** (stats not saved)
3. **Statistics screen not refreshing** (now fixed with onResume)

**Test by winning a game and checking the toast notification and Logcat!**

