package com.organisation.ui.employee.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.organisation.ui.employee.EmployeeHomeScreen
import com.organisation.ui.navigation.Destination
import com.organisation.ui.navigation.NavAction

data object EmployeeHomeDestination: Destination {
    override val key: String
        get() = "EmployeeHomeDestination"
    override val metadata: Map<String, Any>
        get() = emptyMap()
    override val content: @Composable ((modifier: Modifier, metadata: Map<String, Any>, navAction: NavAction) -> Unit) =
        { modifier,_, navAction ->
            EmployeeHomeScreen(
                modifier
            )
        }
}