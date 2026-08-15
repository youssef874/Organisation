package com.organisation.ui.splash.model

sealed interface SplashEvent {
    object AuthNavigationHandled : SplashEvent

    object HomeNavigationHandled : SplashEvent

}
