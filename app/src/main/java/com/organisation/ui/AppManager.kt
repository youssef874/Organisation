package com.organisation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.organisation.ui.navigation.NavigationManager
import com.organisation.ui.splash.model.SplashDestination


@Composable
fun LauncherApps(modifier: Modifier = Modifier) {
    NavigationManager(
        SplashDestination(modifier)
    )
}