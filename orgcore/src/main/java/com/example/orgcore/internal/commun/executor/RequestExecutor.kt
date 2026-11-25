package com.example.orgcore.internal.commun.executor

import com.example.orgcore.internal.commun.model.RequestConstraint
import com.example.orgcore.internal.commun.model.RequestResult

internal interface RequestExecutor<T, G : RequestConstraint> {
    suspend fun execute(constraint: G): RequestResult<T>
}
