package com.example.orgcore.internal.account.Authentication.model

import com.example.orgcore.internal.commun.model.RequestConstraint
import com.example.orgcore.internal.commun.model.RequestPriority
import com.example.orgcore.internal.commun.model.RequestType

internal abstract class AuthenticationRequestConstraint(
    override val requestType: @RequestType String = RequestType.AUTHENTICATION,
    priority: Int = RequestPriority.PRIORITY_HIGH,
) : RequestConstraint


internal data class LoginRequestConstrains(
    override val businessType: String = AuthenticationBusinessType.LOGIN,
    val username: String,
    val password: String
): AuthenticationRequestConstraint()


