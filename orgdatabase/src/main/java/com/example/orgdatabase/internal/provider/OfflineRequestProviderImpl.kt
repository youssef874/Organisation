package com.example.orgdatabase.internal.provider

import com.example.orgdatabase.LoggerHelper
import com.example.orgdatabase.internal.dao.OfflineRequestDao
import com.example.orgdatabase.internal.mapper.toDBOfflineRequest
import com.example.orgdatabase.internal.mapper.toOfflineRequestEntity
import com.example.orgdatabase.public.model.DBOfflineRequest

internal class OfflineRequestProviderImpl(
    private val offlineRequestDao: OfflineRequestDao
) : OfflineRequestProvider {

    companion object {
        private const val CLASS_NAME = "OfflineRequestProviderImpl"
        private const val TAG = "OfflineRequest"
    }

    override suspend fun addRequest(request: DBOfflineRequest): Boolean {
        LoggerHelper.logger.d(TAG, "$CLASS_NAME addRequest uuid: ${request.requestUuid}, type: ${request.requestType}")
        return try {
            val id = offlineRequestDao.insert(request.toOfflineRequestEntity())
            id != -1L
        } catch (e: Exception) {
            LoggerHelper.logger.e(TAG, "$CLASS_NAME addRequest exception: ${e.message}")
            false
        }
    }

    override suspend fun addRequests(requests: List<DBOfflineRequest>): Boolean {
        LoggerHelper.logger.i(TAG, "$CLASS_NAME|addRequests| size: ${requests.size}")
        return try {
            val results = offlineRequestDao.insert(requests.map { it.toOfflineRequestEntity() })
            results.all { it != -1L }
        } catch (e: Exception) {
            LoggerHelper.logger.e(TAG, "$CLASS_NAME addRequests exception: ${e.message}")
            false
        }
    }

    override suspend fun getNextBatchToSync(limit: Int): List<DBOfflineRequest> {
        return try {
            offlineRequestDao.getNextBatchToSync(limit).map { it.toDBOfflineRequest() }
        } catch (e: Exception) {
            LoggerHelper.logger.e(TAG, "$CLASS_NAME getNextBatchToSync exception: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getNextBatchToSyncByType(requestType: String, limit: Int): List<DBOfflineRequest> {
        return try {
            offlineRequestDao.getNextBatchToSyncByType(requestType, limit).map { it.toDBOfflineRequest() }
        } catch (e: Exception) {
            LoggerHelper.logger.e(TAG, "$CLASS_NAME getNextBatchToSyncByType exception: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getByRequestUuid(requestUuid: String): DBOfflineRequest? {
        return try {
            offlineRequestDao.getByRequestUuid(requestUuid)?.toDBOfflineRequest()
        } catch (e: Exception) {
            LoggerHelper.logger.e(TAG, "$CLASS_NAME getByRequestUuid exception: ${e.message}")
            null
        }
    }

    override suspend fun deleteByRequestUuid(requestUuid: String): Boolean {
        return try {
            val deletedCount = offlineRequestDao.deleteByRequestUuid(requestUuid)
            deletedCount > 0
        } catch (e: Exception) {
            LoggerHelper.logger.e(TAG, "$CLASS_NAME deleteByRequestUuid exception: ${e.message}")
            false
        }
    }

    override suspend fun getCount(): Int {
        return try {
            offlineRequestDao.getCount()
        } catch (e: Exception) {
            LoggerHelper.logger.e(TAG, "$CLASS_NAME getCount exception: ${e.message}")
            0
        }
    }

    override suspend fun deleteAll(): Boolean {
        return try {
            offlineRequestDao.deleteAll()
            true
        } catch (e: Exception) {
            LoggerHelper.logger.e(TAG, "$CLASS_NAME deleteAll exception: ${e.message}")
            false
        }
    }
}
