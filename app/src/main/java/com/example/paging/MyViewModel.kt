package com.example.paging


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel() {
    fun searchData(query:String)=repository.getContentsByName(query).cachedIn(viewModelScope)
    val responseData=repository.getData().cachedIn(viewModelScope)
    fun searchDataFromApi(query:String)=repository.searchDataByQuery(query).cachedIn(viewModelScope)
 }