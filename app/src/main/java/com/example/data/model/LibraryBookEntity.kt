package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "library_books")
data class LibraryBookEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String, // tracks which logged-in user owns this book entry
    val title: String,
    val author: String,
    val totalChapters: Int,
    val currentChapter: Int,
    val notes: String = "",
    val status: String = "Reading", // Reading, Completed, Wishlist
    val coverId: Long? = null,
    val dateAdded: Long = System.currentTimeMillis()
)
