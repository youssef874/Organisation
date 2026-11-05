package com.organisation.ui.pendinglist.viewmodel

import android.app.Application
import com.organisation.domaine.attendance.model.AttendanceRecord
import com.organisation.ui.common.viewmodel.BaseViewModel
import com.organisation.ui.pendinglist.model.PendingListUiEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class PendingListViewModel @Inject constructor(
    application: Application
): BaseViewModel<PendingListUiEvents>(application) {

    companion object{
        private const val CLASS_NAME = "PendingListViewModel"
        private const val TAG = "ATTENDANCE"
    }

    private val _pendingList = MutableStateFlow<List<AttendanceRecord>>(emptyList())
    val pendingList: StateFlow<List<AttendanceRecord>> = _pendingList.asStateFlow()

    override fun handleEvent(event: PendingListUiEvents) {

    }
}