package com.mindbloom.app.ui.navigation

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mindbloom.app.data.seed.DemoDataSeeder
import com.mindbloom.app.di.AppContainer
import com.mindbloom.app.ui.screens.HabitDetailsScreen
import com.mindbloom.app.ui.screens.HabitsScreen
import com.mindbloom.app.ui.screens.HomeScreen
import com.mindbloom.app.ui.screens.InsightsScreen
import com.mindbloom.app.ui.screens.JournalScreen
import com.mindbloom.app.ui.screens.LoginScreen
import com.mindbloom.app.ui.screens.MeditationScreen
import com.mindbloom.app.ui.screens.MoodScreen
import com.mindbloom.app.ui.screens.OnboardingScreen
import com.mindbloom.app.ui.screens.ProfileScreen
import com.mindbloom.app.ui.screens.RegisterScreen
import com.mindbloom.app.ui.screens.SettingsScreen
import com.mindbloom.app.ui.screens.SplashScreen
import com.mindbloom.app.ui.screens.StatisticsScreen
import com.mindbloom.app.ui.screens.SuccessScreen
import com.mindbloom.app.ui.theme.MB
import com.mindbloom.app.ui.theme.MindBloomTheme
import com.mindbloom.app.ui.viewmodel.AuthViewModel
import com.mindbloom.app.ui.viewmodel.HabitDetailsViewModel
import com.mindbloom.app.ui.viewmodel.HabitsViewModel
import com.mindbloom.app.ui.viewmodel.HomeViewModel
import com.mindbloom.app.ui.viewmodel.InsightsViewModel
import com.mindbloom.app.ui.viewmodel.JournalViewModel
import com.mindbloom.app.ui.viewmodel.MindBloomViewModelFactory
import com.mindbloom.app.ui.viewmodel.MoodViewModel
import com.mindbloom.app.ui.viewmodel.ProfileViewModel
import com.mindbloom.app.ui.viewmodel.SettingsViewModel
import com.mindbloom.app.ui.viewmodel.StatisticsViewModel
import com.mindbloom.app.ui.viewmodel.ThemeViewModel

/** Root composable: theme, scaffold, bottom bar and the navigation graph. */
@Composable
fun MindBloomApp(container: AppContainer) {
    val factory = remember { MindBloomViewModelFactory(container) }
    val themeViewModel: ThemeViewModel = viewModel(factory = factory)
    val darkMode by themeViewModel.darkMode.collectAsStateWithLifecycle()

    MindBloomTheme(darkTheme = darkMode) {
        val navController = rememberNavController()
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route

        // Shared across the habits list, the details screen and the celebration.
        val habitsViewModel: HabitsViewModel = viewModel(factory = factory)
        val celebration by habitsViewModel.celebration.collectAsStateWithLifecycle()

        LaunchedEffect(celebration) {
            celebration?.let {
                habitsViewModel.consumeCelebration()
                navController.navigate(Routes.success(Uri.encode(it.habitName), it.streak))
            }
        }

        Scaffold(
            bottomBar = {
                if (currentRoute != null && currentRoute in Routes.bottomBarRoutes) {
                    MindBloomBottomBar(
                        currentRoute = currentRoute,
                        onSelect = { route -> navController.navigateToTab(route) }
                    )
                }
            },
            containerColor = MB.colors.background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Routes.SPLASH,
                modifier = Modifier.padding(innerPadding)
            ) {
                /* ------------------------------ Splash ---------------------- */
                composable(Routes.SPLASH) {
                    SplashScreen(
                        onFinished = {
                            val prefs = container.preferences
                            val destination = when {
                                !prefs.onboardingCompleted -> Routes.ONBOARDING
                                prefs.isSignedIn -> Routes.HOME
                                else -> Routes.LOGIN
                            }
                            navController.navigate(destination) {
                                popUpTo(Routes.SPLASH) { inclusive = true }
                            }
                        }
                    )
                }

                /* ---------------------------- Onboarding -------------------- */
                composable(Routes.ONBOARDING) {
                    val authViewModel: AuthViewModel = viewModel(factory = factory)
                    OnboardingScreen(
                        onFinished = {
                            authViewModel.completeOnboarding()
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(Routes.ONBOARDING) { inclusive = true }
                            }
                        }
                    )
                }

                /* ------------------------------- Auth ----------------------- */
                composable(Routes.LOGIN) {
                    val authViewModel: AuthViewModel = viewModel(factory = factory)
                    LaunchedEffect(Unit) {
                        authViewModel.prefillDemoAccount(
                            DemoDataSeeder.DEMO_EMAIL,
                            DemoDataSeeder.DEMO_PASSWORD
                        )
                    }
                    LoginScreen(
                        viewModel = authViewModel,
                        onLoggedIn = {
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        },
                        onSignUp = { navController.navigate(Routes.REGISTER) }
                    )
                }

                composable(Routes.REGISTER) {
                    val authViewModel: AuthViewModel = viewModel(factory = factory)
                    RegisterScreen(
                        viewModel = authViewModel,
                        onRegistered = {
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        },
                        onLogIn = { navController.popBackStack() }
                    )
                }

                /* ------------------------------- Tabs ----------------------- */
                composable(Routes.HOME) {
                    val homeViewModel: HomeViewModel = viewModel(factory = factory)
                    HomeScreen(
                        viewModel = homeViewModel,
                        onOpenMood = { navController.navigateToTab(Routes.MOOD) },
                        onOpenHabits = { navController.navigateToTab(Routes.HABITS) },
                        onOpenStatistics = { navController.navigate(Routes.STATISTICS) },
                        onOpenInsights = { navController.navigate(Routes.INSIGHTS) },
                        onOpenProfile = { navController.navigateToTab(Routes.PROFILE) }
                    )
                }

                composable(Routes.MOOD) {
                    val moodViewModel: MoodViewModel = viewModel(factory = factory)
                    MoodScreen(
                        viewModel = moodViewModel,
                        onSaved = { navController.navigateToTab(Routes.HOME) }
                    )
                }

                composable(Routes.HABITS) {
                    HabitsScreen(
                        viewModel = habitsViewModel,
                        onOpenHabit = { id -> navController.navigate(Routes.habitDetails(id)) }
                    )
                }

                composable(Routes.JOURNAL) {
                    val journalViewModel: JournalViewModel = viewModel(factory = factory)
                    JournalScreen(
                        viewModel = journalViewModel,
                        onSaved = { navController.navigateToTab(Routes.HOME) }
                    )
                }

                composable(Routes.PROFILE) {
                    val profileViewModel: ProfileViewModel = viewModel(factory = factory)
                    ProfileScreen(
                        viewModel = profileViewModel,
                        onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                        onOpenProgress = { navController.navigate(Routes.STATISTICS) },
                        onOpenMeditation = { navController.navigate(Routes.MEDITATION) }
                    )
                }

                /* --------------------------- Secondary ---------------------- */
                composable(Routes.STATISTICS) {
                    val statisticsViewModel: StatisticsViewModel = viewModel(factory = factory)
                    StatisticsScreen(viewModel = statisticsViewModel)
                }

                composable(Routes.INSIGHTS) {
                    val insightsViewModel: InsightsViewModel = viewModel(factory = factory)
                    InsightsScreen(
                        viewModel = insightsViewModel,
                        onViewWeeklyReport = { navController.navigate(Routes.STATISTICS) },
                        onOpenMeditation = { navController.navigate(Routes.MEDITATION) }
                    )
                }

                composable(Routes.MEDITATION) {
                    MeditationScreen(onBack = { navController.popBackStack() })
                }

                composable(Routes.SETTINGS) {
                    val settingsViewModel: SettingsViewModel = viewModel(factory = factory)
                    SettingsScreen(
                        viewModel = settingsViewModel,
                        onBack = { navController.popBackStack() },
                        onSignedOut = {
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }

                /* ------------------------- Habit details -------------------- */
                composable(
                    route = Routes.HABIT_DETAILS,
                    arguments = listOf(navArgument("habitId") { type = NavType.LongType })
                ) { entry ->
                    val habitId = entry.arguments?.getLong("habitId") ?: 0L
                    val detailsViewModel: HabitDetailsViewModel = viewModel(
                        key = "habit_$habitId",
                        factory = MindBloomViewModelFactory(container, habitId)
                    )
                    HabitDetailsScreen(
                        detailsViewModel = detailsViewModel,
                        habitsViewModel = habitsViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                /* ---------------------------- Success ----------------------- */
                composable(
                    route = Routes.SUCCESS,
                    arguments = listOf(
                        navArgument("habitName") { type = NavType.StringType },
                        navArgument("streak") { type = NavType.IntType }
                    )
                ) { entry ->
                    val name = Uri.decode(entry.arguments?.getString("habitName") ?: "your habit")
                    val streak = entry.arguments?.getInt("streak") ?: 0
                    SuccessScreen(
                        habitName = name,
                        streak = streak,
                        onViewProgress = {
                            navController.navigate(Routes.STATISTICS) {
                                popUpTo(Routes.SUCCESS) { inclusive = true }
                            }
                        },
                        onBackToHome = { navController.navigateToTab(Routes.HOME) }
                    )
                }
            }
        }
    }
}

/** Tab navigation that keeps a single copy of each destination on the stack. */
private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(Routes.HOME) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
