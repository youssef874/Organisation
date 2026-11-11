package com.example.orgcore.internal.account

import com.example.orgcore.internal.internal.datastore.DataStoreItemProvider

internal interface AccountSettings {

    val isUserAccountSynchronized: DataStoreItemProvider<Boolean>

    val isRelatedUserAccountSynchronized: DataStoreItemProvider<Boolean>

    val isUserAttendanceRapportSynchronized: DataStoreItemProvider<Boolean>
}