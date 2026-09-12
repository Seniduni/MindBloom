# MindBloom

A mood, habit and journalling companion for Android, built from the supplied UI
designs. Kotlin + Jetpack Compose + Room, MVVM throughout, no backend required.

---

## Opening the project

1. Unzip `MindBloom.zip`.
2. Android Studio → **File ▸ Open** → select the `MindBloom` folder (the one
   containing `settings.gradle.kts`). Do not open the `app` folder directly.
3. Let Gradle sync. The first sync downloads the Gradle distribution and the
   AndroidX artifacts, so it needs an internet connection and a few minutes.
4. Run the `app` configuration on an emulator or device (API 26+).

Tested toolchain — matches Android Studio **Meerkat 2024.3.1**:

| Component | Version |
|---|---|
| Android Gradle Plugin | 8.7.3 |
| Gradle | 8.10.2 |
| Kotlin | 2.0.21 |
| KSP | 2.0.21-1.0.28 |
| Compose BOM | 2024.12.01 |
| Room | 2.6.1 |
| WorkManager | 2.9.1 |
| compileSdk / targetSdk | 35 |
| minSdk | 26 |
| JDK | 17 (bundled with Android Studio) |

If Gradle sync complains about the JDK, set **Settings ▸ Build Tools ▸ Gradle ▸
Gradle JDK** to the bundled JetBrains Runtime 17.

---

## Demo account

The database seeds itself on first launch. The Login screen is pre-filled with:

```
Email:    punara@email.com
Password: password123
```

You can also register a fresh account — the seeded habits and moods are shared
(this is a single-user local app), but the profile switches to the new user.

To wipe everything and re-seed, uninstall the app or clear its storage.

---

## Screens

| # | Screen | Route | Notes |
|---|---|---|---|
| 1 | Splash | `splash` | Animated logo, then routes to onboarding / login / home |
| 2–4 | Onboarding | `onboarding` | Three-page pager, Skip and Get Started |
| 5 | Login | `login` | Validation, remember me, Google (mocked), forgot-password dialog |
| 6 | Register | `register` | Full validation, wellness goal picker |
| 7 | Dashboard | `home` | Greeting, mood banner, habit progress, streaks, weekly chart, AI banner |
| 8 | Mood Tracker | `mood` | Five moods, 200-char note, month calendar with navigation |
| 9 | My Habits | `habits` | Live progress rows, streak pills, FAB, create/edit sheet |
| 10 | Habit Details | `habit_details/{id}` | Progress controls, week ticks, edit, delete |
| 11 | Journal | `journal` | 1000-char editor, mood, photo thumbnails, recent entries |
| 12 | Statistics | `statistics` | Week/Month/Year, mood + energy chart, donut, month grid |
| 13 | AI Insights | `insights` | Wellness score and generated insights |
| 14 | Meditation | `meditation` | Category filter, working countdown player with breathing circle |
| 15 | Profile | `profile` | XP bar, achievements, goals / progress / badges / edit |
| 16 | Settings | `settings` | Dark mode, notifications, reminder, language, log out |
| 17 | Success | `success/{name}/{streak}` | Fires automatically when a habit is completed |

Every interactive element is wired. Nothing is a dead button.

---

## Architecture

```
UI (Compose)  →  ViewModel  →  Repository  →  Room DAO / SharedPreferences
```

```
app/src/main/java/com/mindbloom/app/
├── MainActivity.kt              Compose host, edge-to-edge
├── MindBloomApplication.kt      Seeds demo data on first launch
├── di/AppContainer.kt           Service locator (no Hilt, deliberately)
├── notifications/               Reminder channel, worker, scheduler
├── data/
│   ├── Models.kt                Mood, HabitWithProgress, DayCompletion…
│   ├── StaticContent.kt         Meditation library, badges, palettes
│   ├── InsightEngine.kt         Rule-based analyser behind AI Insights
│   ├── local/                   Entities, DAOs, MindBloomDatabase
│   ├── prefs/AppPreferences.kt  Session, onboarding, settings
│   ├── repository/              Habit, Mood, Journal, User
│   └── seed/DemoDataSeeder.kt   First-run content
├── ui/
│   ├── theme/                   Colours, Poppins type scale, light/dark
│   ├── components/              Buttons, fields, cards, charts, illustrations
│   ├── navigation/              Routes, bottom bar, NavGraph
│   ├── screens/                 One file per screen
│   └── viewmodel/               One ViewModel per feature + shared factory
└── util/                        DateUtils, Security, Validators
```

### Where the numbers come from

Nothing on screen is hard-coded. Streaks, percentages and charts are all
derived from the `habit_logs` and `moods` tables:

- **Habit streak** — consecutive days where `progress >= target`, counted back
  from today (or yesterday if today is still open).
- **Dashboard streak** — consecutive days where the mean per-habit completion
  reached 60%. The mean is used rather than a weighted total so a 6,000 step
  target does not drown out a target of one.
- **"4/6" habit progress** — partial credit: each habit contributes the share
  of its target reached, and the sum is rounded. A day that is 69% done reads
  as 4 of 6.
- **Wellness score** — 60% mood average, 40% habit completion, over the last
  seven days, compared against the seven before that.

### The "AI"

`InsightEngine` correlates logged moods against habit completion and reports
what it finds — for example, comparing mood on days following a completed
sleep habit against days that did not. It is transparent and reproducible
rather than a black box, which matters more than novelty for a wellbeing app.
If there is not enough data yet, it says so instead of inventing a pattern.

---

## Daily reminders

Reminders are real, not a stored preference that does nothing.

- **Scheduling** — `ReminderScheduler` enqueues a unique periodic `WorkRequest`
  with an initial delay calculated to the next occurrence of the time chosen in
  Settings, repeating every 24 hours. WorkManager cannot fire at an exact
  wall-clock time, so expect a few minutes of drift; that is the right trade
  for a wellbeing nudge, because it survives reboots and needs no exact-alarm
  permission.
- **Content** — `ReminderWorker` reads the live database and names what is
  actually outstanding: "Still to do: drink water and reading. Your 7 day
  streak is still alive." If everything is done and the mood is logged, it
  stays quiet.
- **Permission** — on Android 13+ the Settings toggle requests
  `POST_NOTIFICATIONS` before enabling, and explains what happened if the user
  declines. On older versions the toggle just works.
- **Lifecycle** — the schedule is re-applied on app start and whenever the
  toggle or the reminder time changes, so the two settings and the background
  work never drift apart.

To test without waiting, temporarily set the initial delay in
`ReminderScheduler.schedule` to `1` minute, or trigger the worker from
**App Inspection ▸ Background Task Inspector** in Android Studio.

---

## Assets

No binary image assets are needed. Everything is generated:

- **Logo, onboarding art, Bloom Bot** — Compose `Canvas` vector drawings in
  `ui/components/Illustrations.kt`, so they scale to any density.
- **Launcher icon** — adaptive icon from a vector drawable.
- **Poppins** — the four weights used by the design ship in `res/font/`.
- **Habit and mood icons** — system emoji, matching the mock-ups exactly.

---

## Things worth knowing

- **Dark mode really works.** The Settings toggle writes to preferences, which
  a `ThemeViewModel` observes at the activity level, so the whole app repaints
  immediately. Brand accents stay fixed; only surfaces and text swap.
- **Passwords are hashed** (SHA-256) even though storage is local. A production
  build should use a salted KDF such as Argon2 or bcrypt.
- **Google sign-in is mocked.** It creates or reuses a local account. Wiring
  real Google Sign-In needs a Firebase project and a client ID.
- **Data persists** across restarts via Room; the app has no network permission
  and never uploads anything.

---

## If the build complains

| Symptom | Fix |
|---|---|
| "Unsupported Gradle version" | Let Studio use the wrapper — do not point it at a local Gradle |
| KSP errors after changing an entity | `Build ▸ Clean Project`, then rebuild |
| Fonts not found | Confirm `app/src/main/res/font/` contains the five `.ttf` files |
| Seeded data looks wrong | Uninstall the app so the seed guard resets |
| Reminder never arrives | Check the toggle is on, the permission is granted, and battery optimisation is not restricting the app |
