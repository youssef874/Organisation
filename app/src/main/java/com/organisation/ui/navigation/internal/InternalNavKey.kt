package com.organisation.ui.navigation.internal

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
internal data class InternalNavKey(
    val id: String
): NavKey
