package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.LibraryBookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryBookDao {
    @Query("SELECT * FROM library_books WHERE username = :username ORDER BY dateAdded DESC")
    fun getBooksForUser(username: String): Flow<List<LibraryBookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: LibraryBookEntity): Long

    @Update
    suspend fun updateBook(book: LibraryBookEntity)

    @Delete
    suspend fun deleteBook(book: LibraryBookEntity)

    @Query("DELETE FROM library_books WHERE id = :bookId")
    suspend fun deleteBookById(bookId: Int)
}
