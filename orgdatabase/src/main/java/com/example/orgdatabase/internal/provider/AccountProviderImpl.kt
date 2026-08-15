package com.example.orgdatabase.internal.provider

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.withTransaction
import com.example.orgdatabase.LoggerHelper
import com.example.orgdatabase.internal.dao.AccountDao
import com.example.orgdatabase.internal.dao.EmailDao
import com.example.orgdatabase.internal.dao.PhoneNumberDao
import com.example.orgdatabase.internal.db.OrganisationDatabase
import com.example.orgdatabase.internal.entity.EmailEntity
import com.example.orgdatabase.internal.entity.PhoneNumberEntity
import com.example.orgdatabase.internal.mapper.extractEmails
import com.example.orgdatabase.internal.mapper.extractPhoneNumbers
import com.example.orgdatabase.internal.mapper.toAccountEntity
import com.example.orgdatabase.internal.mapper.toDBAccount
import com.example.orgdatabase.public.model.DBAccount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class AccountProviderImpl(
    private val db: OrganisationDatabase,
    private val accountDao: AccountDao,
    private val emailDao: EmailDao,
    private val phoneNumberDao: PhoneNumberDao
) : AccountProvider {

    companion object{
        private const val CLASS_NAME = "AccountProviderImpl"
        private const val TAG = "Account"
    }

    override suspend fun addAccountWithDetails(
        dbAccount: DBAccount
    ): Boolean {
        LoggerHelper.logger.d(TAG, "$CLASS_NAME addAccountWithDetails id: ${dbAccount.accountType}, emails: ${dbAccount.emails
        }, phoneNumbers: ${dbAccount.phoneNumbers}")
        return try {
            db.withTransaction {
                val accountId = accountDao.insert(dbAccount.toAccountEntity())
                if (accountId != -1L) {
                    emailDao.insert(dbAccount.extractEmails(accountId))
                    phoneNumberDao.insert(dbAccount.extractPhoneNumbers(accountId))
                    LoggerHelper.logger.d(
                        TAG,
                        "$CLASS_NAME addAccountWithDetails new account id: $accountId"
                    )
                    true
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            LoggerHelper.logger.e(TAG, "$CLASS_NAME addAccountWithDetails exception: ${e.message}")
            false
        }
    }

    override suspend fun addAccountsWitDetails(accounts: List<DBAccount>): Boolean {
        LoggerHelper.logger.i(
            TAG,
            "$CLASS_NAME|addAccountsWitDetails| size: ${accounts.size}"
        )
        return try {
            db.withTransaction {
                accounts.all { account ->
                    val accountId = accountDao.insert(account.toAccountEntity())
                    if (accountId != -1L) {
                        emailDao.insert(account.extractEmails(accountId))
                        phoneNumberDao.insert(account.extractPhoneNumbers(accountId))
                        true
                    } else {
                        false
                    }
                }
            }
        } catch (e: Exception) {
            LoggerHelper.logger.e(TAG, "$CLASS_NAME addAccountsWitDetails exception: ${e.message}")
            false
        }
    }

    override suspend fun deleteAccountWithDetails(accountId: Long): Boolean {
        LoggerHelper.logger.d(TAG, "$CLASS_NAME deleteAccountWithDetails id: $accountId")
        return try {
            val account = accountDao.getAccountById(accountId)
            if (account != null) {
                accountDao.delete(account).let {
                    LoggerHelper.logger.d(TAG, "$CLASS_NAME deleteAccountWithDetails deleted: $it")
                    it > 0
                }
            } else {
                LoggerHelper.logger.w(TAG, "$CLASS_NAME deleteAccountWithDetails account not found")
                false
            }
        } catch (e: Exception) {
            LoggerHelper.logger.e(TAG, "$CLASS_NAME deleteAccountWithDetails exception: ${e.message}")
            false
        }
    }

    override suspend fun updateAccount(account: DBAccount): Boolean {
        LoggerHelper.logger.d(TAG, "$CLASS_NAME updateAccount id: ${account.id}")
        return try {
            accountDao.update(account.toAccountEntity().copy(id = account.id)).let {
                LoggerHelper.logger.d(TAG, "$CLASS_NAME updateAccount updated: $it")
                it > 0
            }
        } catch (e: Exception) {
            LoggerHelper.logger.e(TAG, "$CLASS_NAME updateAccount exception: ${e.message}")
            false
        }
    }

    override suspend fun updateEmails(accountId: Long, emails: List<String>): Boolean {
        LoggerHelper.logger.d(TAG, "$CLASS_NAME updateEmails id: $accountId, emails: ${emails.size}")
        return try {
            emailDao.deleteEmailsByAccountId(accountId)
            emailDao.insert(emails.map { EmailEntity(email = it,accountId =accountId ) }).let {
                LoggerHelper.logger.d(TAG, "$CLASS_NAME updateEmails inserted: $it")
                it.size == emails.size
            }
        } catch (e: Exception) {
            LoggerHelper.logger.e(TAG, "$CLASS_NAME updateEmails exception: ${e.message}")
            false
        }
    }

    override suspend fun updatePhoneNumbers(accountId: Long, phoneNumbers: List<String>): Boolean {
        LoggerHelper.logger.d(TAG, "$CLASS_NAME updatePhoneNumbers id: $accountId, phoneNumbers: ${phoneNumbers.size}")
        return try {
            phoneNumberDao.deletePhoneNumbersByAccountId(accountId)
            phoneNumberDao.insert(phoneNumbers.map { PhoneNumberEntity(number = it, accountId = accountId) }).let {
                LoggerHelper.logger.d(TAG, "$CLASS_NAME updatePhoneNumbers inserted: $it")
                it.size == phoneNumbers.size
            }
        } catch (e: Exception) {
            LoggerHelper.logger.e(TAG, "$CLASS_NAME updatePhoneNumbers exception: ${e.message}")
            false
        }
    }

    override suspend fun getAccountWithDetailsById(accountId: Long): DBAccount? {
        return accountDao.getAccountWithDetailsByIdSuspend(accountId)?.toDBAccount()
    }

    override fun observeAccountWithDetailsById(accountId: Long): Flow<DBAccount?> {
        return accountDao.getAccountWithDetailsById(accountId).map { it?.toDBAccount() }
    }

    override suspend fun getAccountsByChunk(page: Int, pageSize: Int): List<DBAccount> {
        return accountDao.getAccountsByChunk(pageSize, pageSize*page).map { it.toDBAccount() }
    }

    override fun getAccountsPaged(): PagingSource<Int, DBAccount> {
        return MappedPagingSource(
            accountDao.getAccountsWithDetailsPaged()
        ) { it.toDBAccount() }
    }
}

private class MappedPagingSource<Key : Any, Value : Any, NewValue : Any>(
    private val originalSource: PagingSource<Key, Value>,
    private val mapper: (Value) -> NewValue
) : PagingSource<Key, NewValue>() {

    override fun getRefreshKey(state: PagingState<Key, NewValue>): Key? {
        return null
    }

    override suspend fun load(params: LoadParams<Key>): LoadResult<Key, NewValue> {
        return when (val result = originalSource.load(params)) {
            is LoadResult.Page -> LoadResult.Page(
                data = result.data.map(mapper),
                prevKey = result.prevKey,
                nextKey = result.nextKey,
                itemsBefore = result.itemsBefore,
                itemsAfter = result.itemsAfter
            )
            is LoadResult.Error -> LoadResult.Error(result.throwable)
            is LoadResult.Invalid -> LoadResult.Invalid()
        }
    }

    override val jumpingSupported: Boolean get() = originalSource.jumpingSupported
    override val keyReuseSupported: Boolean get() = originalSource.keyReuseSupported

    init {
        originalSource.registerInvalidatedCallback { invalidate() }
    }
}
