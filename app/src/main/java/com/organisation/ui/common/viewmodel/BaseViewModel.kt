package com.organisation.ui.common.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<EVENT>(application: Application) : AndroidViewModel(application) {

    private val eventFlow = MutableSharedFlow<EVENT>()

    init {
        viewModelScope.launch {
            eventFlow.collect { event ->
                handleEvent(event)
            }
        }
    }

    fun onEvent(event: EVENT) {
        viewModelScope.launch {
            eventFlow.emit(event)
        }
    }

    protected abstract fun handleEvent(event: EVENT)


}