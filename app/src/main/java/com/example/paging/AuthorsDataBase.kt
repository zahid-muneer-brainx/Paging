package com.example.paging

import androidx.room.*



@Database(
    entities = [Result::class, RemoteDataKeys::class],
    version = 1
    )
abstract class AuthorsDataBase : RoomDatabase() {
    abstract fun authorDao(): AuthorDao
    abstract fun getRemoteKeysDao(): RemoteKeysDao
}