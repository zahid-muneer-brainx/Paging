package com.example.paging

sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T) : ResultWrapper<T>()
    data class LoadLoading(val status: Boolean = true) : ResultWrapper<Nothing>()
    data class GenericError(val error: ErrorResponse? = null) :
        ResultWrapper<Nothing>()

    // object class NetworkError : ResultWrapper<Nothing>()
}

data class ErrorResponse(var message: String, var code: Int? = null)
