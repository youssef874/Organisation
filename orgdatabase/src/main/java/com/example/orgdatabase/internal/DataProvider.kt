package com.example.orgdatabase.internal

import android.content.Context
import com.example.orgdatabase.internal.db.OrganisationDatabase
import com.example.orgdatabase.internal.provider.AccountProvider
import com.example.orgdatabase.internal.provider.AccountProviderImpl

internal object DataProvider {

    fun getAccountProvider(context: Context): AccountProvider {
        val db = OrganisationDatabase.getInstance(context)
        return AccountProviderImpl(
            db = db,
            accountDao = db.accountDao(),
            emailDao = db.emailDao(),
            phoneNumberDao = db.phoneNumberDao()
        )
    }
}