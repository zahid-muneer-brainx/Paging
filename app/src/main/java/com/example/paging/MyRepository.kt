package com.example.paging

import android.nfc.tech.MifareUltralight
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject



class MyRepository @Inject constructor(
    private val myApiClient: MyApiClient,
    private val authorsDataBase: AuthorsDataBase
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getData()=Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100
        ),
        pagingSourceFactory = {
            authorsDataBase.authorDao().getAuthors()
        },
        remoteMediator = AuthorsRemoteMediator(
            myApiClient,
            authorsDataBase
        )
    ).liveData

     fun getContentsByName(query:String)=Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100
        ),
        pagingSourceFactory = {
            authorsDataBase.authorDao().searchByContent(query)
        }
    ).liveData

    fun searchDataByQuery(query:String)=Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100
        ),
        pagingSourceFactory = {
            MyPagingSource(myApiClient,query)
        }
    ).liveData
}

