# Testing Guide for Settings Features

## Quick Test Instructions

### 1. Test Sound Effects 🔊

**Steps:**
1. Open the app
2. Go to Settings (bottom-right card on main screen)
3. Make sure "Sound Effects" toggle is ON
4. Go back to main menu
5. Click any button - you should hear a "beep" sound
6. Click "Play Now" to start a game
7. Make a move (tap any cell) - you should hear a different tone
8. Win/lose/draw a game - you should hear result sounds

**Expected Results:**
- ✅ Hear click sounds on all buttons
- ✅ Hear move sounds when placing X or O
- ✅ Hear celebration sound when you win
- ✅ Hear different sound when AI wins
- ✅ Hear neutral sound on draw

**To Disable:**
- Go to Settings → Toggle "Sound Effects" OFF
- All sounds should stop immediately

---

### 2. Test Vibration 📳

**Steps:**
1. Make sure your phone is NOT in silent/vibrate mode (so you can feel the vibrations)
2. Open Settings
3. Make sure "Vibration" toggle is ON
4. Toggle it OFF then ON - you should feel a vibration
5. Go back to main menu
6. Click any button - you should feel a light vibration
7. Click "Play Now" to start a game
8. Make a move - you should feel a medium vibration
9. Win/lose a game - you should feel different vibration patterns

**Expected Results:**
- ✅ Feel vibration when toggling setting
- ✅ Feel light tap on menu buttons
- ✅ Feel medium vibration when making moves
- ✅ Feel pattern vibration (multiple pulses) when you win
- ✅ Feel different pattern when you lose

**Different Vibration Strengths:**
- Menu navigation: Very light (10ms)
- Play Now button: Medium (25ms)
- Game moves: Heavier (50ms)
- Win: Multiple short pulses (celebration!)
- Lose: Two longer pulses

**To Disable:**
- Go to Settings → Toggle "Vibration" OFF
- All vibrations should stop

---

### 3. Test Animations ✨

**Steps:**
1. Open Settings
2. Make sure "Animations" toggle is ON
3. Go back to main menu
4. Click any button - you should see it scale down slightly then back up
5. Click "Play Now" to start a game
6. Make a move - you should see the X or O "pop in" with a bounce effect
7. Try to click an already-filled cell - you should see it shake
8. Win a game - the three winning cells should pulse/glow

**Expected Results:**
- ✅ Buttons scale down/up when clicked (squeeze effect)
- ✅ X and O symbols animate in with bounce
- ✅ Invalid moves trigger shake animation
- ✅ Winning cells pulse/scale up and down

**Animation Types:**
- Button clicks: Scale 90% → 100%
- Cell fills: Scale 0% → 100% with overshoot
- Invalid moves: Horizontal shake
- Winning cells: Pulse animation (scale up → down)

**To Disable:**
- Go to Settings → Toggle "Animations" OFF
- All animations stop (instant changes only)

---

### 4. Test Background Music 🎵

**Steps:**
1. Open Settings
2. Toggle "Background Music" ON
3. Currently this is a placeholder (no actual music plays)

**Note:** 
To enable actual background music:
1. Add an MP3/OGG file to `app/src/main/res/raw/background_music.mp3`
2. Uncomment the music loading code in `SoundManager.java` (line ~145)
3. Rebuild the app

**Expected Results:**
- ✅ Toggle works without crashes
- ⚠️  No actual music plays (placeholder only)

---

### 5. Combined Test (All Features Together) 🎮

**Steps:**
1. Enable ALL features in Settings:
   - ✅ Sound Effects ON
   - ✅ Background Music ON
   - ✅ Vibration ON
   - ✅ Animations ON
   - ✅ Dark Mode ON

2. Go back and play a full game:
   - Click "Play Now" → Should see animation, hear sound, feel vibration
   - Make moves → Should animate in, make sound, vibrate
   - Win the game → Should see winning animation, hear win sound, feel celebration vibration

3. Disable ALL features:
   - ❌ Sound Effects OFF
   - ❌ Background Music OFF
   - ❌ Vibration OFF
   - ❌ Animations OFF

4. Play again → Everything should be instant/silent

**Expected Results:**
- ✅ All features work together smoothly
- ✅ No lag or performance issues
- ✅ Disabling features works instantly
- ✅ Settings persist after closing and reopening app

---

## Troubleshooting

### No Sound?
- Check your phone volume (not on mute)
- Make sure "Sound Effects" is enabled in Settings
- Try toggling the setting OFF then ON

### No Vibration?
- Check if your device supports vibration
- Make sure the app has vibration permission
- Some phones have "vibration intensity" settings - check system settings

### Animations Laggy?
- This is normal on very old devices
- Try disabling animations for better performance
- Animations use hardware acceleration and should be smooth on modern devices

### Settings Not Saving?
- Make sure you have storage permissions
- Try clearing app data and trying again
- Settings are stored in SharedPreferences (should work on all devices)

---

## Performance Notes

- **Battery Impact:** Minimal when disabled, very low when enabled
- **CPU Usage:** Animations use hardware acceleration (GPU)
- **Memory:** All managers use singleton pattern (one instance per app)
- **Permissions:** Only VIBRATE permission required

---

## What's Working ✅

1. ✅ Sound effects for all game actions
2. ✅ Haptic feedback (vibration) with different patterns
3. ✅ Smooth animations for UI elements
4. ✅ Settings toggles work and persist
5. ✅ Real-time feedback when toggling settings
6. ✅ All features can be independently enabled/disabled
7. ✅ Integrated into GameActivity and MainActivity
8. ✅ Lifecycle-aware (pauses/resumes correctly)

## Known Limitations ⚠️

1. ⚠️  Background music is placeholder only (no audio file)
2. ⚠️  Sound effects use system beeps (no custom sound files)
3. ⚠️  Some very old devices may not support vibration patterns

## Next Steps 🚀

To enhance these features:
1. Add custom sound effect files (MP3/OGG)
2. Add background music track
3. Create more elaborate animations
4. Add volume controls for sound
5. Add vibration intensity settings

