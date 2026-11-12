package com.example.orgcore.api.account.event

import com.example.orgcore.api.model.OrgEvent
import com.example.orgcore.api.model.SyncResult


data class AccountFetchDataResult(
    val result: SyncResult
): OrgEvent(
    type = AccountEventTypes.ACCOUNT_FETCH_RESULT
)