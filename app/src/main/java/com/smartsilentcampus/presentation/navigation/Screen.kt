package com.smartsilentcampus.presentation.navigation

sealed class Screen(val route: String, val title: String) {
    object Onboarding : Screen("onboarding", "Welcome")
    object Home : Screen("home", "Dashboard")
    object Locations : Screen("locations", "Locations")
    object AddLocation : Screen("add_location", "Add Location")
    object Profiles : Screen("profiles", "Sound Profiles")
    object History : Screen("history", "History")
    object Settings : Screen("settings", "Settings")
    object TestMode : Screen("test_mode", "Test Mode")
}
