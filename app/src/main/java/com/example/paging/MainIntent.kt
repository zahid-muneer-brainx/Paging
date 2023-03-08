package com.example.paging

sealed class MainIntent {
    data class FetchAuthor(val query:String) : MainIntent()
   data class SearchAuthor(val query:String) : MainIntent()
}