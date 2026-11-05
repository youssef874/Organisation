package com.organisation.ui.employee.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Construction
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.Home
import com.organisation.R
import com.organisation.ui.common.components.home_tabs.TabItem

object Tabs {

    val home = TabItem(
        id = 0,
        icon = Icons.Outlined.Home,
        title = "",
        titleRes = R.string.home
    )

    val attendance = TabItem(
        id = 1,
        icon = Icons.Outlined.EventAvailable,
        title = "",
        titleRes = R.string.attendance
    )

    val tools = TabItem(
        id = 2,
        icon = Icons.Outlined.Construction,
        title = "",
        titleRes = R.string.tools
    )

    val profile = TabItem(
        id = 3,
        icon = Icons.Outlined.AccountCircle,
        title = "",
        titleRes = R.string.profile
    )

    val employeeHomeTabs = listOf(home, attendance, tools, profile)

    val all = listOf(home, attendance, tools, profile)

    fun getTabById(id: Int): TabItem = all.find { it.id == id } ?: home
}