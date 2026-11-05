package com.organisation.ui.splash.model

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.organisation.ui.common.NavigationEvents
import com.organisation.ui.common.NavigationViewModel
import com.organisation.ui.employee.model.EmployeeHomeDestination
import com.organisation.ui.login.model.LoginDestination
import com.organisation.ui.navigation.Destination
import com.organisation.ui.navigation.NavAction
import com.organisation.ui.splash.SplashScreen

data object SplashDestination: Destination {

    override val key: String
        get() = "SplashDestination"
    override val metadata: Map<String, Any>
        get() = emptyMap()
    override val content: @Composable ((modifier: Modifier, metadata: Map<String, Any>, navAction: NavAction) -> Unit) =
        {modifier,_,navAction->
            val activity = LocalActivity.current as ComponentActivity
            val viewModel: NavigationViewModel = hiltViewModel(activity)
            SplashScreen(
                modifier = modifier,
                navigateToBossHome = {

                },
                navigateToWorkerHome = {
                    val destination = EmployeeHomeDestination
                    navAction.navigateClearPrevious(destination)
                    viewModel.onEvent(NavigationEvents.UpdateInitialDestination(destination))
                },
                navigateToManagerHome = {

                },
                navigateToLoginScreen = {
                    val destination = LoginDestination
                    navAction.navigateClearPrevious(destination)
                    viewModel.onEvent(NavigationEvents.UpdateInitialDestination(destination))
                }
            )
        }
}