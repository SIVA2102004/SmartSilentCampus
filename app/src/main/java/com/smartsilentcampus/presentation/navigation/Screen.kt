package com.smartsilentcampus.presentation.navigation

sealed class Screen(val route: String, val title: String) {
    data object Onboarding : Screen("onboarding", "Welcome")
    data object Home : Screen("home", "Dashboard")
    data object Locations : Screen("locations", "Locations")
    data object AddLocation : Screen("add_location", "Add Location")
    data object Profiles : Screen("profiles", "Sound Profiles")
    data object History : Screen("history", "History")
    data object Settings : Screen("settings", "Settings")
    data object TestMode : Screen("test_mode", "Test Mode")
}
