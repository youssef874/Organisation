package com.organisation.ui.common.components.home_tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessAlarm
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.organisation.OrganisationPreviews
import com.organisation.ui.theme.AppButtonColor
import com.organisation.ui.theme.AppPrimaryColor
import com.organisation.ui.theme.OrganisationTheme


@Composable
 fun BottomNav(
    modifier: Modifier = Modifier,
    currentItem: TabItem,
    items: List<TabItem>,
    onItemClicked:(TabItem)-> Unit
) {
    var currentTab by rememberSaveable {
        mutableIntStateOf(currentItem.id)
    }

    NavigationBar(
        modifier = modifier,
        containerColor = AppPrimaryColor
    ) {
        items.forEach { tabItem ->
            NavigationBarItem(
                selected = tabItem.id == currentItem.id,
                icon = {
                    Icon(imageVector = tabItem.icon,
                        contentDescription = tabItem.title)
                },
                label = {
                    Text(
                        text = if (tabItem.titleRes != -1){
                            stringResource(tabItem.titleRes)
                        }else{
                            tabItem.title
                        }
                    )
                },
                onClick = {
                    currentTab = tabItem.id
                    onItemClicked(tabItem)
                },
                colors = NavigationBarItemDefaults.colors().copy(
                    selectedIconColor = AppButtonColor,
                    selectedTextColor = AppButtonColor
                )
            )
        }
    }
}

internal val ITEMS = listOf(
    TabItem(
        id = 1,
        icon = Icons.Outlined.Person,
        title = "tab1"
    ),
    TabItem(
        id = 2,
        icon = Icons.Outlined.Android,
        title = "tab2"
    ),
    TabItem(
        id = 3,
        icon = Icons.Outlined.AccessAlarm,
        title = "tab3"
    )
)

@OrganisationPreviews
@Composable
internal fun BottomNavPreview() {

    var value = remember {
        ITEMS[0].title
    }

    OrganisationTheme {
        Scaffold(
            bottomBar = {
                BottomNav(
                    currentItem = ITEMS[0],
                    items = ITEMS,
                    onItemClicked = {
                        value = it.title
                    }
                )
            }
        ) { innerPadding->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ){
                Text(text = value)
            }
        }
    }
}