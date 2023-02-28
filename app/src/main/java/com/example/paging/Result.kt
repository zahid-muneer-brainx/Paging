package com.example.paging

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Authors")
data class Result(
    @PrimaryKey(autoGenerate = false)
    val _id: String,
    @ColumnInfo(name = "author")
    val author: String,
    @ColumnInfo(name = "authorSlug")
    val authorSlug: String,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "dateAdded")
    val dateAdded: String,
    @ColumnInfo(name = "dateModified")
    val dateModified: String,
    @ColumnInfo(name = "length")
    val length: Int,
    @ColumnInfo(name = "page")
    var page: Int?,
)