package com.organisation.ui.login.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.organisation.ui.login.LoginScreen
import com.organisation.ui.navigation.Destination
import com.organisation.ui.navigation.NavAction
import com.organisation.ui.splash.model.SplashDestination

class LoginDestination(
    private val modifier: Modifier = Modifier
): Destination {

    override val key: String
        get() = "LoginDestination"

    override val metadata
    : Map<String, Any>
        get() = emptyMap()
    override val content: @Composable ((metadata: Map<String, Any>, navAction: NavAction) -> Unit)
        get() = {_,navAction->
            LoginScreen(
                navigateToSplashScreen = {
                    navAction.navigateClearPrevious(SplashDestination(modifier))
                }
            )
        }
}