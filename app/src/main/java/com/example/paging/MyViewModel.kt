package com.example.paging


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel() {
    val userIntent = Channel<MainIntent>(Channel.UNLIMITED)
    val _state = MutableStateFlow<ViewModelState>(ViewModelState.idle)
    val state: StateFlow<ViewModelState>
        get() = _state

    fun searchData(query: String) = repository.getContentsByName(query).cachedIn(viewModelScope)
    fun searchDataFromApi(query: String) =
        repository.searchDataByQuery(query).cachedIn(viewModelScope)

    private fun handleIntent() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect {
                when (it) {
                    is MainIntent.FetchAuthor -> getAuthors(MainIntent.FetchAuthor(it.query))
                    is MainIntent.SearchAuthor -> getAuthors(MainIntent.SearchAuthor(it.query))
                }
            }
        }
    }

    private fun getAuthors(action: MainIntent) {
        viewModelScope.launch {
            when (action){
                is MainIntent.FetchAuthor -> {
                    _state.value = try {
                        ViewModelState.Authors(repository.getData(action.query).cachedIn(viewModelScope))
                    }
                        catch (e: Exception) {
                            ViewModelState.Error(e.localizedMessage)
                        }
                }
                is MainIntent.SearchAuthor -> {
                    _state.value = try {
                        ViewModelState.Authors(repository.getContentsByName(action.query).cachedIn(viewModelScope))
                    } catch (e: Exception) {
                        ViewModelState.Error(e.localizedMessage)
                    }
                }
            }
        }
        println("abcde")
    }

    init {

        handleIntent()
    }
}