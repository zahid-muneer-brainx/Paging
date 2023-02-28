package com.example.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

@OptIn(ExperimentalPagingApi::class)
class AuthorsRemoteMediator(
    private val apiService: MyApiClient,
    private val authorDatabase: AuthorsDataBase,
) : RemoteMediator<Int, Result>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Result>
    ): MediatorResult {
        val page: Int = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: 1
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
        }
        try {

            val apiResponse = apiService.getData(page = page)
                val results = apiResponse.results
                val endOfPaginationReached = results.isEmpty()
                if (loadType == LoadType.REFRESH) {
                    CoroutineScope(Dispatchers.IO).launch {
                        authorDatabase.getRemoteKeysDao().clearRemoteKeys()
                        authorDatabase.authorDao().clearAllAuthors()
                    }
                }
                val prevKey = if (page > 1) page - 1 else null
                val nextKey = if (endOfPaginationReached) null else page + 1
                val remoteKeys = results.map {
                    RemoteDataKeys(
                        authorId = it._id,
                        prevKey = prevKey,
                        currentPage = page,
                        nextKey = nextKey
                    )
                }
                CoroutineScope(Dispatchers.IO).launch {
                    authorDatabase.getRemoteKeysDao().insertAll(remoteKeys)
                    authorDatabase.authorDao()
                        .insertAll(results.onEachIndexed { _, movie -> movie.page = page })
                }

                return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (error: IOException) {
            return MediatorResult.Error(error)
        } catch (error: HttpException) {
            return MediatorResult.Error(error)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Result>): RemoteDataKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?._id?.let { id ->
                authorDatabase.getRemoteKeysDao().getRemoteKeyByAuthorID(id)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Result>): RemoteDataKeys? {
        return state.pages.firstOrNull {
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let { author ->
            authorDatabase.getRemoteKeysDao().getRemoteKeyByAuthorID(author._id)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Result>): RemoteDataKeys? {
        return state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { author ->
            authorDatabase.getRemoteKeysDao().getRemoteKeyByAuthorID(author._id)
        }
    }
}