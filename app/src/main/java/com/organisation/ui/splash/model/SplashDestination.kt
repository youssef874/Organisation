package com.organisation.ui.splash.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.organisation.ui.employee.home.model.EmployeeHomeDestination
import com.organisation.ui.login.model.LoginDestination
import com.organisation.ui.navigation.Destination
import com.organisation.ui.navigation.NavAction
import com.organisation.ui.splash.SplashScreen

class SplashDestination(
    private val modifier: Modifier = Modifier
): Destination {
    override val key: String
        get() = "SplashDestination"
    override val metadata: Map<String, Any>
        get() = emptyMap()
    override val content: @Composable ((metadata: Map<String, Any>, navAction: NavAction) -> Unit) =
        {_,navAction->
            SplashScreen(
                navigateToBossHome = {

                },
                navigateToWorkerHome = {
                    navAction.navigateClearPrevious(EmployeeHomeDestination(modifier))
                },
                navigateToManagerHome = {

                },
                navigateToLoginScreen = {
                    navAction.navigateClearPrevious(LoginDestination(modifier))
                }
            )
        }
}