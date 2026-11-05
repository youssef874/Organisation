package com.organisation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface Destination {

    val key: String

    val metadata: Map<String, Any>
    val content: @Composable (modifier: Modifier, metadata: Map<String, Any>, navAction: NavAction) -> Unit
}