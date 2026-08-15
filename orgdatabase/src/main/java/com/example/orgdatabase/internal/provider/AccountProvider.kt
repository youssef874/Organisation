package com.example.orgdatabase.internal.provider

import androidx.paging.PagingSource
import com.example.orgdatabase.public.model.DBAccount
import kotlinx.coroutines.flow.Flow

/**
 * Interface for providing account data from the local database.
 */
internal interface AccountProvider {

    /**
     * Adds a single account with its details (emails, phone numbers) to the database.
     *
     * @param dbAccount The account data to add.
     * @return `true` if successful, `false` otherwise.
     */
    suspend fun addAccountWithDetails(dbAccount: DBAccount): Boolean

    /**
     * Adds a list of accounts with their details to the database in a batch.
     *
     * @param accounts The list of accounts to add.
     * @return `true` if all accounts were added successfully, `false` otherwise.
     */
    suspend fun addAccountsWitDetails(accounts: List<DBAccount>): Boolean

    /**
     * Deletes an account and all its related details from the database.
     *
     * @param accountId The ID of the account to delete.
     * @return `true` if the account was found and deleted, `false` otherwise.
     */
    suspend fun deleteAccountWithDetails(accountId: Long): Boolean

    /**
     * Updates an existing account's primary information.
     *
     * @param account The updated account data.
     * @return `true` if the update was successful, `false` otherwise.
     */
    suspend fun updateAccount(account: DBAccount): Boolean

    /**
     * Updates the emails associated with a specific account.
     *
     * @param accountId The ID of the account.
     * @param emails The new list of emails.
     * @return `true` if the update was successful, `false` otherwise.
     */
    suspend fun updateEmails(accountId: Long, emails: List<String>): Boolean

    /**
     * Updates the phone numbers associated with a specific account.
     *
     * @param accountId The ID of the account.
     * @param phoneNumbers The new list of phone numbers.
     * @return `true` if the update was successful, `false` otherwise.
     */
    suspend fun updatePhoneNumbers(accountId: Long, phoneNumbers: List<String>): Boolean

    /**
     * Retrieves a single account with its details by ID.
     *
     * @param accountId The ID of the account to retrieve.
     * @return The [DBAccount] if found, or `null` otherwise.
     */
    suspend fun getAccountWithDetailsById(accountId: Long): DBAccount?

    /**
     * Observes changes to a specific account and its details.
     *
     * @param accountId The ID of the account to observe.
     * @return A [Flow] emitting the [DBAccount] whenever it changes.
     */
    fun observeAccountWithDetailsById(accountId: Long): Flow<DBAccount?>

    /**
     * Retrieves a chunk (page) of accounts from the database.
     *
     * @param page The page number (starting from 0).
     * @param pageSize The number of accounts per page.
     * @return A list of [DBAccount]s for the requested chunk.
     */
    suspend fun getAccountsByChunk(page: Int, pageSize: Int): List<DBAccount>

    /**
     * Returns a [PagingSource] for paginated access to all accounts with details.
     */
    fun getAccountsPaged(): PagingSource<Int, DBAccount>
}