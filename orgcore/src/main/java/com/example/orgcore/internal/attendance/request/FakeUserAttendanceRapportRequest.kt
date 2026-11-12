package com.example.orgcore.internal.attendance.request

import com.example.orgcore.api.model.NetworkResult
import com.example.orgcore.internal.account.Authentication.provider.AuthenticationProvider
import com.example.orgcore.internal.attendance.model.Attendance
import com.example.orgcore.internal.commun.FakeData
import com.example.orgcore.internal.commun.ServerRequest

internal class FakeUserAttendanceRapportRequest(
    private val authenticationProvider: AuthenticationProvider,
    var attendanceOverride: List<Attendance>? = null,
    var responseCode: Int = 200,
    var shouldThrowException: Boolean = false
): ServerRequest<List<Attendance>> {
    
    override suspend fun request(): NetworkResult<List<Attendance>> {
        if (shouldThrowException) {
            throw RuntimeException("Unexpected server error")
        }
        if (attendanceOverride != null) {
            return NetworkResult.Success(attendanceOverride!!)
        }
        val accountType = authenticationProvider.authenticatedUserAccountTypeDataStoreItemProvider
            .getData(-1)
        val data = FakeData.getFakeData(accountType).attendance
        return if (responseCode == 200){
            NetworkResult.Success(data)
        }else{
            NetworkResult.Error(
                code = responseCode,
                message = "Error"
            )
        }
    }
}