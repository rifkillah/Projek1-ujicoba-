package com.example.data.repository

import android.content.Context
import com.example.data.api.OpenLibraryClient
import com.example.data.database.AppDatabase
import com.example.data.model.LibraryBookEntity
import com.example.data.model.SubjectResponse
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LibraryRepository(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val userDao = database.userDao()
    private val libraryBookDao = database.libraryBookDao()

    // Session Management State
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser

    // -------------------------------------------------------------
    // User Authentication Operations
    // -------------------------------------------------------------
    suspend fun registerUser(username: String, passwordHash: String): Boolean {
        val existing = userDao.getUserByUsername(username)
        if (existing != null) return false // user already exists
        val user = UserEntity(username = username, passwordHash = passwordHash)
        val result = userDao.registerUser(user)
        return result > 0
    }

    suspend fun loginUser(username: String, passwordHash: String): Boolean {
        val user = userDao.getUserByUsername(username) ?: return false
        if (user.passwordHash == passwordHash) {
            _currentUser.value = user
            return true
        }
        return false
    }

    fun logout() {
        _currentUser.value = null
    }

    // -------------------------------------------------------------
    // Personal Library DAO Operations (Reactive Flow for active user)
    // -------------------------------------------------------------
    fun getMyBooksFlow(username: String): Flow<List<LibraryBookEntity>> {
        return libraryBookDao.getBooksForUser(username)
    }

    suspend fun addBookToLibrary(book: LibraryBookEntity): Boolean {
        val result = libraryBookDao.insertBook(book)
        return result > 0
    }

    suspend fun updateBookProgress(book: LibraryBookEntity) {
        libraryBookDao.updateBook(book)
    }

    suspend fun deleteBookFromLibrary(book: LibraryBookEntity) {
        libraryBookDao.deleteBook(book)
    }

    suspend fun deleteBookById(id: Int) {
        libraryBookDao.deleteBookById(id)
    }

    // -------------------------------------------------------------
    // Popular Books API Operations
    // -------------------------------------------------------------
    suspend fun getPopularBooksBySubject(subject: String): SubjectResponse {
        return OpenLibraryClient.apiService.getBooksBySubject(subject)
    }
}
