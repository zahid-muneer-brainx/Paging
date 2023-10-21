package com.example.paging

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MyApiClient {

    @GET("/quotes")
    suspend fun getData(
        @Query("page") page: Int
    ):DataResponse

    @GET("/search/quotes")
    suspend fun searchData(
        @Query("query") page: String
    ):DataResponse
}
