package com.example.orgcore.api.account.event

import androidx.annotation.StringDef

@StringDef(
    AccountEventTypes.ACCOUNT_FETCH_RESULT
)

annotation class AccountEventTypes{
    companion object{
        const val ACCOUNT_FETCH_RESULT = "account_fetch_result"
    }
}
