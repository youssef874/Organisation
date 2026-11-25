package com.example.orgcore.internal.commun.model

internal interface RequestConstraint {
    val requestType: @RequestType String
    val businessType: String
    val priority: Int
        get() = RequestPriority.PRIORITY_NORMAL
}
