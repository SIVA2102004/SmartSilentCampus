package com.smartsilentcampus.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartsilentcampus.permissions.PermissionManager
import com.smartsilentcampus.presentation.history.HistoryScreen
import com.smartsilentcampus.presentation.history.HistoryViewModel
import com.smartsilentcampus.presentation.home.HomeScreen
import com.smartsilentcampus.presentation.home.HomeViewModel
import com.smartsilentcampus.presentation.locations.AddLocationScreen
import com.smartsilentcampus.presentation.locations.LocationListScreen
import com.smartsilentcampus.presentation.locations.LocationsViewModel
import com.smartsilentcampus.presentation.navigation.Screen
import com.smartsilentcampus.presentation.onboarding.OnboardingScreen
import com.smartsilentcampus.presentation.profiles.ProfilesScreen
import com.smartsilentcampus.presentation.profiles.ProfilesViewModel
import com.smartsilentcampus.presentation.settings.SettingsScreen
import com.smartsilentcampus.presentation.settings.SettingsViewModel
import com.smartsilentcampus.presentation.testmode.TestModeScreen
import com.smartsilentcampus.presentation.testmode.TestModeViewModel
import com.smartsilentcampus.presentation.theme.SmartSilentCampusTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var permissionManager: PermissionManager

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartSilentCampusTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route
                    ) {
                        composable(Screen.Onboarding.route) {
                            OnboardingScreen(
                                permissionManager = permissionManager,
                                onFinish = {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(Screen.Home.route) {
                            HomeScreen(
                                viewModel = homeViewModel,
                                onNavigateToAddLocation = { navController.navigate(Screen.AddLocation.route) },
                                onNavigateToLocations = { navController.navigate(Screen.Locations.route) },
                                onNavigateToProfiles = { navController.navigate(Screen.Profiles.route) },
                                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                                onNavigateToTestMode = { navController.navigate(Screen.TestMode.route) }
                            )
                        }

                        composable(Screen.Locations.route) {
                            val locVm: LocationsViewModel = hiltViewModel()
                            LocationListScreen(
                                viewModel = locVm,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToAddLocation = { navController.navigate(Screen.AddLocation.route) }
                            )
                        }

                        composable(Screen.AddLocation.route) {
                            val locVm: LocationsViewModel = hiltViewModel()
                            AddLocationScreen(
                                viewModel = locVm,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Profiles.route) {
                            val profVm: ProfilesViewModel = hiltViewModel()
                            ProfilesScreen(
                                viewModel = profVm,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.History.route) {
                            val histVm: HistoryViewModel = hiltViewModel()
                            HistoryScreen(
                                viewModel = histVm,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Settings.route) {
                            val setVm: SettingsViewModel = hiltViewModel()
                            SettingsScreen(
                                viewModel = setVm,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.TestMode.route) {
                            val testVm: TestModeViewModel = hiltViewModel()
                            TestModeScreen(
                                viewModel = testVm,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.refreshStatus()
    }
}
