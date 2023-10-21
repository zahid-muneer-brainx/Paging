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
    private val authorDatabase: AuthorsDataBase
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

            val apiResponse =  apiService.getData(page = page)
                val results = apiResponse.results
                val endOfPaginationReached = results.isEmpty()
                val prevKey = if (page > 1) page - 1 else null
                val nextKey = if (endOfPaginationReached) null else page + 1
                val remoteKeys = results.map {
                    RemoteDataKeys(
                        authorId = it._id,
                        prevKey = prevKey,
                        currentPage = page,
                        nextKey = nextKey,
                        content = it.content
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
            state.closestItemToPosition(position)?.content?.let { id ->
                authorDatabase.getRemoteKeysDao().getRemoteKeyByContent(id)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Result>): RemoteDataKeys? {
        return state.pages.firstOrNull {
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let { author ->
            authorDatabase.getRemoteKeysDao().getRemoteKeyByContent(author.content)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Result>): RemoteDataKeys? {
        return state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { author ->
            authorDatabase.getRemoteKeysDao().getRemoteKeyByAuthorID(author._id)
        }
    }

    /* This is an implementation of a RemoteMediator class for pagination using the Paging 3 library in Android. The AuthorsRemoteMediator class receives an instance of an API client (apiService) and a database (authorDatabase) as constructor parameters. It overrides the load method of the RemoteMediator class to handle loading data in a unidirectional flow.

The load method takes two parameters: loadType, which is an enum that can be one of LoadType.REFRESH, LoadType.PREPEND, or LoadType.APPEND, and state, which represents the current state of the Paging 3 library.

The code inside the load method uses the loadType parameter to determine how to load the data. For LoadType.REFRESH, it retrieves the page number closest to the current position of the user and uses that to make the API request. For LoadType.PREPEND, it retrieves the page number of the first item in the list and uses that to make the API request. For LoadType.APPEND, it retrieves the page number of the last item in the list and uses that to make the API request.

After retrieving the page number, the code makes an API request using the apiService instance and gets a response containing a list of data items (results). It also sets endOfPaginationReached to true if the response is empty. It then calculates the previous and next page numbers based on the current page number.

Finally, the code inserts the retrieved data and remote keys into the authorDatabase using a coroutine to perform the database operations asynchronously. It returns a MediatorResult.Success object with endOfPaginationReached set to true if the API response is empty, and returns a MediatorResult.Success object with endOfPaginationReached set to false otherwise. If there's an error during the operation, it returns a MediatorResult.Error object.

The class also includes three helper methods: getRemoteKeyClosestToCurrentPosition, getRemoteKeyForFirstItem, and getRemoteKeyForLastItem. These methods are used to retrieve the remote keys from the database for the current page, the first item, and the last item, respectively. The remote keys are used to determine the previous and next page numbers for pagination. */
}