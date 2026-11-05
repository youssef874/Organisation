package com.organisation.ui.common.components.home_tabs

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
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
import androidx.compose.ui.unit.dp
import com.organisation.OrganisationPreviews
import com.organisation.ui.theme.AppButtonColor
import com.organisation.ui.theme.AppPrimaryColor
import com.organisation.ui.theme.OrganisationTheme

@Composable
 fun RailNav(
    modifier: Modifier = Modifier,
    currentItem: TabItem,
    items: List<TabItem>,
    onItemClicked:(TabItem)-> Unit
) {
    var currentTab by rememberSaveable {
        mutableIntStateOf(currentItem.id)
    }
    NavigationRail(
        modifier = modifier,
        containerColor = AppPrimaryColor
    ) {
        items.forEach { navItem ->
            NavigationRailItem(
                selected = navItem.id == currentItem.id,
                icon = {
                    Icon(
                        imageVector = navItem.icon,
                        contentDescription = navItem.title
                    )
                },
                onClick = {
                    currentTab = navItem.id
                    onItemClicked(navItem)
                },
                label = {
                    Text(
                        text = if (navItem.titleRes != -1){
                            stringResource(navItem.titleRes)
                        }else{
                            navItem.title
                        }
                    )
                },
                colors = NavigationRailItemDefaults.colors().copy(
                    selectedIconColor = AppButtonColor,
                    selectedTextColor = AppButtonColor
                )
            )

        }
    }
}

@OrganisationPreviews
@Composable
internal fun RailNavPreview() {
    var value = remember {
        ITEMS[0].title
    }
    OrganisationTheme {
        Scaffold { innerPadding ->
            Row(
                modifier = Modifier.fillMaxSize()
                    .padding(innerPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RailNav(
                    modifier = Modifier.fillMaxHeight(),
                    currentItem = ITEMS[0],
                    items = ITEMS,
                    onItemClicked = {
                        value = it.title
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = value)
            }
        }
    }
}