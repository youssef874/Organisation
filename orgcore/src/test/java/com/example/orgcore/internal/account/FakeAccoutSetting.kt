package com.example.orgcore.internal.account

import com.example.orgcore.internal.FakeDataStoreItemProvider
import com.example.orgcore.internal.internal.datastore.DataStoreItemProvider

internal class FakeAccountSetting: AccountSettings {
    override val isUserAccountSynchronized: DataStoreItemProvider<Boolean> = FakeDataStoreItemProvider()
    override val isRelatedUserAccountSynchronized: DataStoreItemProvider<Boolean> = FakeDataStoreItemProvider()
    override val isUserAttendanceRapportSynchronized: DataStoreItemProvider<Boolean> = FakeDataStoreItemProvider()
}
