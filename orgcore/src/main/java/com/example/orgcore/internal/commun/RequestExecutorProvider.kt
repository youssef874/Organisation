package com.example.orgcore.internal.commun

import com.example.orgcore.internal.account.Authentication.cache.AuthenticationLoginCache
import com.example.orgcore.internal.account.Authentication.executor.LoginRequestExecutor
import com.example.orgcore.internal.account.Authentication.model.AuthenticationRequestConstraint
import com.example.orgcore.internal.account.Authentication.model.LoginRequestConstrains
import com.example.orgcore.internal.account.Authentication.provider.AuthenticationProvider
import com.example.orgcore.internal.account.Authentication.request.FakeAuthenticationRequest
import com.example.orgcore.internal.account.model.Account
import com.example.orgcore.internal.attendance.cache.CancelAttendanceCache
import com.example.orgcore.internal.attendance.executor.AttendanceRequestExecutor
import com.example.orgcore.internal.attendance.executor.CancelAttendanceRequestExecutor
import com.example.orgcore.internal.attendance.model.Attendance
import com.example.orgcore.internal.attendance.model.AttendanceRequestConstraints
import com.example.orgcore.internal.attendance.model.CancelAttendanceRequestConstraints
import com.example.orgcore.internal.attendance.request.FakeCancelAttendanceServerRequest
import com.example.orgcore.internal.commun.cache.OfflineQueueCache
import com.example.orgcore.internal.commun.executor.AppRequestExecutor
import com.example.orgcore.internal.commun.executor.RequestExecutor
import com.example.orgcore.internal.commun.model.RequestConstraint
import com.example.orgcore.internal.network.NetworkMonitor
import com.example.orgdatabase.public.api.AccountDataApi
import com.example.orgdatabase.public.api.OfflineRequestDataApi

internal object RequestExecutorProvider {

    private fun getLoginRequestExecutorProvider(
        networkMonitor: NetworkMonitor,
        authenticationProvider: AuthenticationProvider
    ): RequestExecutor<Account, LoginRequestConstrains> {
        return LoginRequestExecutor(
            networkMonitor = networkMonitor,
            serverRequest = {
                FakeAuthenticationRequest(
                    networkMonitor = networkMonitor,
                    userName = it.username,
                    psw = it.password
                )
            },
            cache = {
                AuthenticationLoginCache(
                    authenticationProvider
                )
            }
        )
    }

    private fun getCancelAttendanceRequestExecutorProvider(
        networkMonitor: NetworkMonitor,
        offlineRequestDataApi: OfflineRequestDataApi,
        accountDataApi: AccountDataApi
    ): RequestExecutor<Attendance, CancelAttendanceRequestConstraints> {
        return CancelAttendanceRequestExecutor(
            networkMonitor = networkMonitor,
            accountDataApi = accountDataApi,
            offlineCache = OfflineQueueCache(
                offlineRequestDataApi
            ),
            serverRequest = {
                FakeCancelAttendanceServerRequest(
                    accountId = it.serverAccountId,
                    date = it.date,
                    accountDataApi = accountDataApi
                )
            },
            cache = {
                CancelAttendanceCache(
                    accountDataApi
                )
            }
        )
    }

    private fun getAttendanceExecutorProvider(
        networkMonitor: NetworkMonitor,
        offlineRequestDataApi: OfflineRequestDataApi,
        accountDataApi: AccountDataApi
    ): RequestExecutor<Attendance, AttendanceRequestConstraints> {
        return AttendanceRequestExecutor(
            cancelAttendanceRequestExecutor = {
                getCancelAttendanceRequestExecutorProvider(
                    networkMonitor,offlineRequestDataApi, accountDataApi
                )
            }
        )
    }

    fun getAppRequestExecutor(
        networkMonitor: NetworkMonitor,
        authenticationProvider: AuthenticationProvider,
        offlineRequestDataApi: OfflineRequestDataApi,
        accountDataApi: AccountDataApi
    ): RequestExecutor<Any, RequestConstraint> {
        return AppRequestExecutor(
            authentificationRequestExecutor = {
                @Suppress("UNCHECKED_CAST")
                getLoginRequestExecutorProvider(
                    networkMonitor,
                    authenticationProvider
                ) as RequestExecutor<Any, AuthenticationRequestConstraint>
            },
            attendanceRequestExecutor = {
                (getAttendanceExecutorProvider(
                    networkMonitor,
                    offlineRequestDataApi,
                    accountDataApi
                ))
            }
        )
    }
}
