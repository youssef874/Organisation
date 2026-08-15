package com.organisation.ui.navigation

import androidx.compose.runtime.Composable

interface Destination {

    val key: String

    val metadata: Map<String, Any>
    val content: @Composable (metadata: Map<String, Any>, navAction: NavAction) -> Unit
}