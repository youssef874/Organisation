package com.example.orgcore.internal

import android.content.Context
import com.example.orgcore.SDKComponent
import com.example.orgcore.commun.OrgEventListener
import com.example.orgcore.commun.OrgEventNotifier
import com.example.orgcore.internal.account.AccountEventHandler
import com.example.orgcore.internal.account.AccountManager
import com.example.orgcore.internal.account.AccountProvider
import com.example.orgcore.internal.account.AccountSettings
import com.example.orgcore.internal.account.AccountSettingsImpl
import com.example.orgcore.internal.account.Authentication.provider.AuthenticationProvider
import com.example.orgcore.internal.account.Authentication.provider.AuthenticationProviderDefault
import com.example.orgcore.internal.account.Authentication.request.FakeAuthenticationRequest
import com.example.orgcore.internal.account.Authentication.service.AuthenticationService
import com.example.orgcore.internal.account.Authentication.service.DefaultAuthenticationService
import com.example.orgcore.internal.account.service.AccountService
import com.example.orgcore.internal.account.service.AccountServiceImpl
import com.example.orgcore.internal.attendance.AttendanceProvider
import com.example.orgcore.internal.attendance.AttendanceProviderDefault
import com.example.orgcore.internal.network.ConnectivityManagerNetworkMonitor
import com.example.orgcore.internal.network.NetworkMonitor
import com.youssefmkh.core.internal.commun.datastore.DataStoreFactory

internal object ServicesProvider {

    fun getNetworkMonitor(): NetworkMonitor =
        ConnectivityManagerNetworkMonitor(SDKComponent.SDKContext.getContext())

    fun getDataStoreServices() = DataStoreFactory

    fun getAuthenticationProvider(context: Context): AuthenticationProvider =
        AuthenticationProviderDefault(
            stringDataStoreProvider = getDataStoreServices().getStringDataStoreProvider(context),
            intDataStoreProvider = getDataStoreServices().getIntegerDataStoreProvider(context)
        )

    fun getAuthenticationService(context: Context): AuthenticationService =
        DefaultAuthenticationService(
            requestFactory = { userName, psw ->
                FakeAuthenticationRequest(getNetworkMonitor(), userName, psw)
            },
            authenticationProvider = getAuthenticationProvider(context),
        )

    fun accountService(context: Context): AccountService =
        AccountServiceImpl.getInstance(context = context)

    fun getAccountEventsDispatcher(): OrgEventNotifier = AccountEventHandler

    fun getAccountEventsListener(): OrgEventListener = AccountEventHandler

    fun getAccountProvider(context: Context): AccountProvider= AccountManager.getInstance(context)

    fun getAccountSettings(context: Context): AccountSettings = AccountSettingsImpl(
        booleanDataStoreProvider = DataStoreFactory.getBooleanDataStoreProvider(context)
    )

    fun getAttendanceProvider(context: Context): AttendanceProvider =
        AttendanceProviderDefault(
            context = context,
            authenticationProvider = getAuthenticationProvider(context)
        )

}
