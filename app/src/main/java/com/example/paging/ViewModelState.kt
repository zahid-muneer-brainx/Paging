package com.example.paging

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

sealed class ViewModelState {
    object idle:ViewModelState()
    object loading:ViewModelState()
    data class Authors(val author: Flow<PagingData<Result>>) : ViewModelState()
    data class Error(val error: String?): ViewModelState()

}