package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.LibraryBookEntity
import com.example.data.model.UserEntity
import com.example.data.model.Work
import com.example.data.repository.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface PopularBooksState {
    object Idle : PopularBooksState
    object Loading : PopularBooksState
    data class Success(val subject: String, val books: List<Work>) : PopularBooksState
    data class Error(val message: String) : PopularBooksState
}

class PocketLibViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LibraryRepository(application)

    // Current Session State
    val currentUser: StateFlow<UserEntity?> = repository.currentUser

    // Bottom Navigation tab: "popular" or "library"
    private val _currentTab = MutableStateFlow("popular")
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    // -----------------------------------------------------------------
    // Reactive Library flow, depends heavily on the logged-in user
    // -----------------------------------------------------------------
    val myBooks: StateFlow<List<LibraryBookEntity>> = currentUser
        .flatMapLatest { user ->
            if (user != null) {
                repository.getMyBooksFlow(user.username)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Auth screen states
    private val _authState = MutableStateFlow<String?>(null) // null = idle, "LOADING", "SUCCESS_REGISTER", "SUCCESS_LOGIN", or error message
    val authState: StateFlow<String?> = _authState.asStateFlow()

    // -----------------------------------------------------------------
    // Popular Books subject selections
    // -----------------------------------------------------------------
    private val _selectedSubject = MutableStateFlow("classic") // default subject
    val selectedSubject: StateFlow<String> = _selectedSubject.asStateFlow()

    private val _popularBooksState = MutableStateFlow<PopularBooksState>(PopularBooksState.Idle)
    val popularBooksState: StateFlow<PopularBooksState> = _popularBooksState.asStateFlow()

    init {
        // Automatically load popular books for initial subject
        fetchPopularBooks("classic")
    }

    fun selectTab(tab: String) {
        _currentTab.value = tab
    }

    fun selectSubject(subject: String) {
        _selectedSubject.value = subject
        fetchPopularBooks(subject)
    }

    fun clearAuthState() {
        _authState.value = null
    }

    // -----------------------------------------------------------------
    // Registration and Login actions
    // -----------------------------------------------------------------
    fun register(username: String, pword: String) {
        if (username.isBlank() || pword.isBlank()) {
            _authState.value = "Username dan password tidak boleh kosong"
            return
        }
        _authState.value = "LOADING"
        viewModelScope.launch {
            try {
                // simple hash simulation
                val isSuccess = repository.registerUser(username, pword)
                if (isSuccess) {
                    _authState.value = "SUCCESS_REGISTER"
                } else {
                    _authState.value = "Username sudah terdaftar"
                }
            } catch (e: Exception) {
                _authState.value = "Registrasi gagal: ${e.localizedMessage}"
            }
        }
    }

    fun login(username: String, pword: String) {
        if (username.isBlank() || pword.isBlank()) {
            _authState.value = "Username dan password tidak boleh kosong"
            return
        }
        _authState.value = "LOADING"
        viewModelScope.launch {
            try {
                val isSuccess = repository.loginUser(username, pword)
                if (isSuccess) {
                    _authState.value = "SUCCESS_LOGIN"
                } else {
                    _authState.value = "Username atau password salah"
                }
            } catch (e: Exception) {
                _authState.value = "Login gagal: ${e.localizedMessage}"
            }
        }
    }

    fun logout() {
        _authState.value = null
        repository.logout()
        _currentTab.value = "popular"
    }

    // -----------------------------------------------------------------
    // API Call to Fetch popular books by subject
    // -----------------------------------------------------------------
    fun fetchPopularBooks(subject: String) {
        _popularBooksState.value = PopularBooksState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getPopularBooksBySubject(subject)
                _popularBooksState.value = PopularBooksState.Success(subject, response.works)
            } catch (e: Exception) {
                _popularBooksState.value = PopularBooksState.Error("Gagal mengambil data buku: ${e.localizedMessage ?: "Cek koneksi internet Anda"}")
            }
        }
    }

    // -----------------------------------------------------------------
    // Personal Reading Library - CRUD operations
    // -----------------------------------------------------------------
    fun addBook(title: String, author: String, totalChapters: Int, currentChapter: Int, notes: String, coverId: Long? = null) {
        val user = currentUser.value ?: return
        if (title.isBlank()) return
        val currentCap = if (currentChapter > totalChapters) totalChapters else currentChapter

        viewModelScope.launch {
            val book = LibraryBookEntity(
                username = user.username,
                title = title,
                author = if (author.isNotBlank()) author else "Penulis Anonim",
                totalChapters = if (totalChapters > 0) totalChapters else 1,
                currentChapter = currentCap,
                notes = notes,
                coverId = coverId,
                status = if (currentCap >= totalChapters && totalChapters > 0) "Completed" else "Reading"
            )
            repository.addBookToLibrary(book)
        }
    }

    fun updateBookProgress(book: LibraryBookEntity, newChapter: Int, newNotes: String, newStatus: String) {
        viewModelScope.launch {
            val updatedCap = if (newChapter > book.totalChapters) book.totalChapters else newChapter
            val resolvedStatus = if (updatedCap >= book.totalChapters) "Completed" else newStatus
            val updatedBook = book.copy(
                currentChapter = updatedCap,
                notes = newNotes,
                status = resolvedStatus
            )
            repository.updateBookProgress(updatedBook)
        }
    }

    fun deleteBook(book: LibraryBookEntity) {
        viewModelScope.launch {
            repository.deleteBookFromLibrary(book)
        }
    }
}
