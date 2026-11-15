package com.example.orgcore.internal.commun.event

import com.example.orgcore.api.model.OrgEvent

internal class FakeOrgEventNotifier : OrgEventNotifier {
    var lastEvent: OrgEvent? = null
    override fun notify(event: OrgEvent) {
        lastEvent = event
    }
}
