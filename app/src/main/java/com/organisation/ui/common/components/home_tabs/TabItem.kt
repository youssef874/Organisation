package com.organisation.ui.common.components.home_tabs

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.graphics.vector.ImageVector
import com.organisation.ui.employee.model.Tabs

data class TabItem(
    val id: Int,
    val icon: ImageVector,
    val title: String,
    val titleRes: Int = -1
)

object TabItemSaver: Saver<TabItem, Any>{
    override fun SaverScope.save(value: TabItem): Any? {
        return value.id
    }

    override fun restore(value: Any): TabItem {
        return (value as? Int)?.let { Tabs.getTabById(it) } ?: Tabs.home
    }
}
