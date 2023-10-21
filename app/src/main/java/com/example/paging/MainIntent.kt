package com.example.paging

sealed class MainIntent {
    object FetchAuthor : MainIntent()
   data class SearchAuthor(val query:String) : MainIntent()
}