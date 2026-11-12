package com.example.orgcore.internal.attendance.sync

import com.example.orgcore.internal.account.AccountSettings
import com.example.orgcore.internal.attendance.cache.UserAttendanceRapportCache
import com.example.orgcore.internal.attendance.model.Attendance
import com.example.orgcore.internal.attendance.request.FakeUserAttendanceRapportRequest
import com.example.orgcore.internal.commun.sync.BaseSynchronizer

internal class UserAttendanceRapportSynchronizer(
    userAttendanceRapportRequest: FakeUserAttendanceRapportRequest,
    private val userAttendanceRapportCache: UserAttendanceRapportCache,
    private val accountSettings: AccountSettings
): BaseSynchronizer<List<Attendance>>(userAttendanceRapportRequest) {
    override val className: String
        get() = "UserAttendanceRapportSynchronizer"
    override val tag: String
        get() = "ATTENDANCE"

    companion object{
        private const val BATCH_SIZE = 200
    }

    override suspend fun updateIsSynchronized(isSynchronized: Boolean) {
        accountSettings.isUserAttendanceRapportSynchronized.updateData(isSynchronized)
    }

    override suspend fun performCaching(data: List<Attendance>): Boolean {
        return data.chunked(BATCH_SIZE).all {
            userAttendanceRapportCache.cache(it)
        }
    }

    override suspend fun isSynchronized(): Boolean {
        return accountSettings.isUserAttendanceRapportSynchronized.getData(false)
    }
}
