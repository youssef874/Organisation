package com.organisation.ui.employee

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.organisation.OrganisationPreviews
import com.organisation.ui.attendance.AttendanceScreen
import com.organisation.ui.common.components.home_tabs.TabItemSaver
import com.organisation.ui.employee.model.Tabs
import com.organisation.ui.theme.AppBackground
import com.organisation.ui.theme.AppBackgroundDark
import com.organisation.ui.theme.AppButtonColor
import com.organisation.ui.theme.AppPrimaryColor
import com.organisation.ui.theme.OrganisationTheme

@Composable
fun EmployeeHomeScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable(stateSaver = TabItemSaver) {
        mutableStateOf(Tabs.home)
    }

    BackHandler(selectedTab != Tabs.home) {
        selectedTab = Tabs.home
    }

    val backgroundColor = if (!isSystemInDarkTheme()) AppBackground else AppBackgroundDark

    val adaptiveInfo = currentWindowAdaptiveInfoV2()
    val layoutType =
        if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            NavigationSuiteType.NavigationRail
        } else {
            NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo)
        }

    val navSuiteItemColors = NavigationSuiteDefaults.itemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = AppButtonColor,
            selectedTextColor = AppButtonColor,
            indicatorColor = AppPrimaryColor
        ),
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            selectedIconColor = AppButtonColor,
            selectedTextColor = AppButtonColor,
            indicatorColor = AppPrimaryColor
        )
    )

    NavigationSuiteScaffold(
        layoutType = layoutType,
        containerColor = backgroundColor,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = AppPrimaryColor,
            navigationRailContainerColor = AppPrimaryColor,
            navigationDrawerContainerColor = AppPrimaryColor
        ),
        navigationSuiteItems = {
            Tabs.employeeHomeTabs.forEach { tab ->
                item(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    colors = navSuiteItemColors,
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = if (tab.titleRes != -1) stringResource(tab.titleRes) else tab.title
                        )
                    },
                    label = {
                        Text(
                            text = if (tab.titleRes != -1) stringResource(tab.titleRes) else tab.title
                        )
                    }
                )
            }
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (selectedTab) {
                Tabs.attendance -> AttendanceScreen(
                    navigateToPendingRequestScreen = {

                    })

                else -> Text(stringResource(selectedTab.titleRes))
            }
        }
    }
}

@OrganisationPreviews
@Composable
fun EmployeeHomeScreenPreview() {
    OrganisationTheme {
        EmployeeHomeScreen()
    }
}
