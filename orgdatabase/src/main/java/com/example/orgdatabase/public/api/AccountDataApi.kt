package com.example.orgdatabase.public.api

import android.content.Context
import com.example.orgdatabase.internal.DataProvider
import com.example.orgdatabase.public.model.DBAccount
import kotlinx.coroutines.flow.Flow


/**
 * Api to retrieve all cashed data related to Account
 */
class AccountDataApi(private val context: Context) {


    /**
     * Add new account to database
     * @param dbAccount: The account to be added [DBAccount]
     * @return true if the account added successfully otherwise return false
     */
    suspend fun addAccountWithDetails(dbAccount: DBAccount): Boolean =
        DataProvider.getAccountProvider(context).addAccountWithDetails(
            dbAccount
        )

    /**
     * Add List of account to database
     * @param dbAccounts: The account list to be added [DBAccount]
     * @return true if the account list added successfully otherwise return false
     */
    suspend fun addAccountsWithDetails(dbAccounts: List<DBAccount>): Boolean =
        DataProvider.getAccountProvider(context).addAccountsWitDetails(dbAccounts)

    /**
     * Delete account from database
     * @param accountId: The account id to be removed this will
     * include all related data emails, phone number ...
     * @return true if the remove is success otherwise return false
     */
    suspend fun deleteAccountWithDetails(accountId: Long): Boolean =
        DataProvider.getAccountProvider(context).deleteAccountWithDetails(accountId)

    /**
     * update the account with the new data
     * @param account: new [DBAccount] instance contain all the changes
     * @return true if the update was successful
     */
    suspend fun updateAccount(account: DBAccount): Boolean =
        DataProvider.getAccountProvider(context).updateAccount(account)

    /**
     * Update the account emails
     * @param accountId: the id of the account emails to be updated
     * @param emails: list of the account emails to be updated
     * @return true if the update was success otherwise return false
     */
    suspend fun updateEmails(accountId: Long, emails: List<String>): Boolean =
        DataProvider.getAccountProvider(context).updateEmails(accountId, emails)

    /**
     * Update the account phone numbers
     * @param accountId: the id of the account phone numbers to be updated
     * @param phoneNumbers: list of the account phone numbers to be updated
     * @return true if the update was success otherwise return false
     */
    suspend fun updatePhoneNumbers(accountId: Long, phoneNumbers: List<String>): Boolean =
        DataProvider.getAccountProvider(context).updatePhoneNumbers(accountId, phoneNumbers)

    /**
     * return one shot account by id
     * @param accountId: the id of the account requested
     * @return the requested account in  [DBAccount] or null if the no cashed account by the requested id
     */
    suspend fun getAccountWithDetailsById(accountId: Long): DBAccount? =
        DataProvider.getAccountProvider(context).getAccountWithDetailsById(accountId)

    /**
     * return the account by id the observe any changes occurs to requested account
     * @param accountId: the id of the account requested
     * @return the requested account in [Flow] of [DBAccount] or null if the no cashed account by the requested id
     */
    fun observeAccountWithDetailsById(accountId: Long): Flow<DBAccount?> =
        DataProvider.getAccountProvider(context).observeAccountWithDetailsById(accountId)

    /**
     * get all accounts by chunk
     * @param page: the page number to be requested
     * @param pageSize: the number of accounts per page
     * @return the requested page of accounts in [List] of [DBAccount]
     */
    suspend fun getAccountsByChunk(page: Int, pageSize: Int): List<DBAccount> =
        DataProvider.getAccountProvider(context).getAccountsByChunk(page, pageSize)

}