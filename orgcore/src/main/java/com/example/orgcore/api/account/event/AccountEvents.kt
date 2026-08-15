package com.example.orgcore.api.account.event

import com.example.orgcore.commun.SyncResult
import com.example.orgcore.commun.model.OrgEvent


data class AccountFetchDataResult(
    val result: SyncResult
): OrgEvent(
    type = AccountEventTypes.ACCOUNT_FETCH_RESULT
)