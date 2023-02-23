package com.example.paging

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException

object SafeAPiCall {


    suspend fun <T> call(
        apiCall: suspend () -> T
    ): ResultWrapper<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall.invoke()
                ResultWrapper.Success(response)
            } catch (throwable: Throwable) {
                when (throwable) {
                    is UnknownHostException -> ResultWrapper.GenericError(
                        ErrorResponse("Network not Available", 503)
                    )
                    is IOException -> ResultWrapper.GenericError(
                        ErrorResponse(throwable.localizedMessage, 4001)
                    )
                    is HttpException -> {
                        val code = throwable.code()
                        var errorResponse = convertErrorBody(throwable)
                        if (errorResponse == null) {
                            errorResponse =
                                ErrorResponse(
                                    "Server Response Failed"
                                )
                        }
                        errorResponse.code = code
                        ResultWrapper.GenericError(errorResponse)
                    }

                    else -> {
                        ResultWrapper.GenericError(
                            ErrorResponse(
                                "Server Response Failed", null
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                ResultWrapper.GenericError(
                    ErrorResponse("Server Response Failed", null)
                )

            }
        }
    }

    private fun convertErrorBody(throwable: HttpException): ErrorResponse? {

        var jobError = throwable.response()?.errorBody()?.string()
        return try {
            var obj = JSONObject(jobError)
            ErrorResponse(
                obj.getString(
                    "Error try Again"
                )

            )

        } catch (exception: Exception) {
            jobError = throwable.message().toString()
            return ErrorResponse(
                jobError
            )

        }
    }

}