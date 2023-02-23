package com.example.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject



class MyRepository @Inject constructor(
    private val myApiClient: MyApiClient
) {
    fun getData()=Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100
        ),
        pagingSourceFactory = {
            MyPagingSource(myApiClient)
        }
    ).liveData
}