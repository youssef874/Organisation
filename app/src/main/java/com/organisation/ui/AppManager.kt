package com.organisation.ui

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.organisation.ui.common.NavigationViewModel
import com.organisation.ui.navigation.NavigationManager


@Composable
fun LauncherApps(modifier: Modifier = Modifier) {
    val activity = LocalActivity.current as ComponentActivity
    val viewModel: NavigationViewModel = hiltViewModel(activity)
    val initialDestination by viewModel.initialDestination.collectAsState()

    NavigationManager(
        modifier,
        initialDestination
    )
}