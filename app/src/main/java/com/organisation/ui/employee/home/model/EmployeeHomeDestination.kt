package com.organisation.ui.employee.home.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.organisation.ui.employee.home.EmployeeHomeScreen
import com.organisation.ui.navigation.Destination
import com.organisation.ui.navigation.NavAction

class EmployeeHomeDestination(private val modifier: Modifier): Destination {
    override val key: String
        get() = "EmployeeHomeDestination"
    override val metadata: Map<String, Any>
        get() = emptyMap()
    override val content: @Composable ((metadata: Map<String, Any>, navAction: NavAction) -> Unit)=
        { _, navAction ->
            EmployeeHomeScreen(
                modifier = modifier
            )
        }
}