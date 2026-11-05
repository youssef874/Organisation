package com.organisation.ui.login.model

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.organisation.ui.common.NavigationEvents
import com.organisation.ui.common.NavigationViewModel
import com.organisation.ui.login.LoginScreen
import com.organisation.ui.navigation.Destination
import com.organisation.ui.navigation.NavAction
import com.organisation.ui.splash.model.SplashDestination

data object LoginDestination: Destination {

    override val key: String
        get() = "LoginDestination"

    override val metadata
    : Map<String, Any>
        get() = emptyMap()
    override val content: @Composable ((modifier: Modifier, metadata: Map<String, Any>, navAction: NavAction) -> Unit)
        get() = {modifier,_,navAction->
            val activity = LocalActivity.current as ComponentActivity
            val viewModel: NavigationViewModel = hiltViewModel(activity)
            LoginScreen(
                navigateToSplashScreen = {
                    val destination = SplashDestination
                    navAction.navigateClearPrevious(destination)
                    viewModel.onEvent(NavigationEvents.UpdateInitialDestination(destination))
                }
            )
        }
}