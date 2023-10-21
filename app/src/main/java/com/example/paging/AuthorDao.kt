package com.example.paging

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AuthorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<Result>)

    @Query("Select * From Authors Order By page")
    fun getAuthors(): PagingSource<Int, Result>

    @Query("Delete From Authors")
    suspend fun clearAllAuthors()

    @Query("Select * from Authors where content LIKE :query")
     fun searchByContent(query:String):PagingSource<Int, Result>
}