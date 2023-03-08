package com.example.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import javax.inject.Inject


class MyRepository @Inject constructor(
    private val myApiClient: MyApiClient,
    private val authorsDataBase: AuthorsDataBase
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getData(query: String) = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100
        ),
        pagingSourceFactory = {
            authorsDataBase.authorDao().searchByContent(query)
        },
        remoteMediator = AuthorsRemoteMediator(
            myApiClient,
            authorsDataBase,
            query
        )
    ).flow

    @OptIn(ExperimentalPagingApi::class)
    fun getContentsByName(query: String) = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100
        ),
        pagingSourceFactory = {
            authorsDataBase.authorDao().searchByContent(query)
        },
        remoteMediator = AuthorsRemoteMediator(
            myApiClient,
            authorsDataBase,
            query
        )
    ).flow

    fun searchDataByQuery(query: String) = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100
        ),
        pagingSourceFactory = {
            MyPagingSource(myApiClient, query)
        }
    ).liveData
}

