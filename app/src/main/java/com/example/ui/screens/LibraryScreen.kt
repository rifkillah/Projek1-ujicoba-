package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.LibraryBookEntity
import com.example.ui.theme.GlassColors
import com.example.ui.theme.appleLiquidGlass
import com.example.ui.viewmodel.PocketLibViewModel

@Composable
fun LibraryScreen(
    viewModel: PocketLibViewModel,
    modifier: Modifier = Modifier
) {
    val myBooks by viewModel.myBooks.collectAsState()

    // Dialog state for adding a book manually
    var showAddManualDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newAuthor by remember { mutableStateOf("") }
    var newTotalCap by remember { mutableStateOf("10") }
    var newCurCap by remember { mutableStateOf("0") }
    var newNotes by remember { mutableStateOf("") }

    // Dialog state for updating/deleting an item
    var bookToUpdate by remember { mutableStateOf<LibraryBookEntity?>(null) }
    var updateChapter by remember { mutableStateOf("") }
    var updateNotes by remember { mutableStateOf("") }
    var updateStatus by remember { mutableStateOf("") }

    // Stats calculations
    val totalBooksCount = myBooks.size
    val completedBooksCount = myBooks.count { it.status == "Completed" }
    val readingBooksCount = myBooks.count { it.status == "Reading" }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Title section
            Text(
                text = "Perpustakaanku",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = GlassColors.TextPrimary
            )
            Text(
                text = "Pantau target membaca dan progres buku fisikmu di sini",
                fontSize = 13.sp,
                color = GlassColors.TextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            // Statistics Ribbon badging
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Total
                StatBadge(
                    title = "Total Buku",
                    value = totalBooksCount.toString(),
                    badgeColor = GlassColors.WhiteGlassBackground,
                    accentColor = GlassColors.TextAccent,
                    modifier = Modifier.weight(1f)
                )
                // Reading
                StatBadge(
                    title = "Dibaca",
                    value = readingBooksCount.toString(),
                    badgeColor = GlassColors.WhiteGlassBackground,
                    accentColor = GlassColors.GlowViolet,
                    modifier = Modifier.weight(1f)
                )
                // Completed
                StatBadge(
                    title = "Selesai",
                    value = completedBooksCount.toString(),
                    badgeColor = GlassColors.WhiteGlassBackground,
                    accentColor = GlassColors.GlowGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            // Display listing
            if (myBooks.isEmpty()) {
                // Empty state card
                EmptyStateCard(onAddClicked = { showAddManualDialog = true })
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(myBooks, key = { it.id }) { book ->
                        LibraryBookItemRow(
                            book = book,
                            onUpdateProgressClicked = {
                                bookToUpdate = book
                                updateChapter = book.currentChapter.toString()
                                updateNotes = book.notes
                                updateStatus = book.status
                            }
                        )
                    }
                }
            }
        }

        // Beautiful glass Floating Action Button for manual addition with gradient alignment
        FloatingActionButton(
            onClick = {
                newTitle = ""
                newAuthor = ""
                newTotalCap = "10"
                newCurCap = "0"
                newNotes = ""
                showAddManualDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 86.dp, end = 20.dp)
                .size(56.dp)
                .testTag("add_book_fab"),
            shape = CircleShape,
            containerColor = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(GlassColors.SoftIndigo, GlassColors.GlowViolet)
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambahkan buku secara manual",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }

    // Modal dialogue: Add Book manually
    if (showAddManualDialog) {
        Dialog(onDismissRequest = { showAddManualDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .appleLiquidGlass(
                        shape = RoundedCornerShape(24.dp),
                        shadowElevation = 14.dp,
                        glassColor = Color(0xF21E293B) // opaque for crisp visibility
                    )
                    .padding(20.dp),
                color = Color.Transparent
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Tambah Buku Manual",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlassColors.TextPrimary
                    )
                    Text(
                        text = "Masukkan detail buku yang sedang dibaca",
                        fontSize = 12.sp,
                        color = GlassColors.TextSecondary,
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )

                    // Inputs
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("manual_title_input"),
                        label = { Text("Judul Buku", color = GlassColors.TextSecondary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = GlassColors.TextPrimary,
                            unfocusedTextColor = GlassColors.TextPrimary,
                            focusedBorderColor = GlassColors.TextAccent,
                            unfocusedBorderColor = GlassColors.GlassBorderLight
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = newAuthor,
                        onValueChange = { newAuthor = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("manual_author_input"),
                        label = { Text("Penulis", color = GlassColors.TextSecondary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = GlassColors.TextPrimary,
                            unfocusedTextColor = GlassColors.TextPrimary,
                            focusedBorderColor = GlassColors.TextAccent,
                            unfocusedBorderColor = GlassColors.GlassBorderLight
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = newTotalCap,
                            onValueChange = { newTotalCap = it },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("manual_total_chapters_input"),
                            label = { Text("Total Bab", color = GlassColors.TextSecondary) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = GlassColors.TextPrimary,
                                unfocusedTextColor = GlassColors.TextPrimary,
                                focusedBorderColor = GlassColors.TextAccent,
                                unfocusedBorderColor = GlassColors.GlassBorderLight
                            )
                        )

                        OutlinedTextField(
                            value = newCurCap,
                            onValueChange = { newCurCap = it },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("manual_current_chapter_input"),
                            label = { Text("Bab Sekarang", color = GlassColors.TextSecondary) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = GlassColors.TextPrimary,
                                unfocusedTextColor = GlassColors.TextPrimary,
                                focusedBorderColor = GlassColors.TextAccent,
                                unfocusedBorderColor = GlassColors.GlassBorderLight
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = newNotes,
                        onValueChange = { newNotes = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("manual_notes_input"),
                        label = { Text("Catatan / Kesan Pertama", color = GlassColors.TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = GlassColors.TextPrimary,
                            unfocusedTextColor = GlassColors.TextPrimary,
                            focusedBorderColor = GlassColors.TextAccent,
                            unfocusedBorderColor = GlassColors.GlassBorderLight
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { showAddManualDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            elevation = null
                        ) {
                            Text("Batal", color = GlassColors.CoralPeach)
                        }

                        Button(
                            onClick = {
                                if (newTitle.isNotBlank()) {
                                    val tot = newTotalCap.toIntOrNull() ?: 10
                                    val cur = newCurCap.toIntOrNull() ?: 0
                                    viewModel.addBook(
                                        title = newTitle,
                                        author = newAuthor,
                                        totalChapters = tot,
                                        currentChapter = cur,
                                        notes = newNotes
                                    )
                                    showAddManualDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(12.dp),
                            enabled = newTitle.isNotBlank()
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(GlassColors.SoftIndigo, GlassColors.GlowViolet)
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 20.dp, vertical = 8.dp)
                            ) {
                                Text("Simpan", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal dialogue: Update progress (U) & Delete book (D) (Perfect SQLite read-write CRUD)
    if (bookToUpdate != null) {
        val activeBook = bookToUpdate!!
        Dialog(onDismissRequest = { bookToUpdate = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .appleLiquidGlass(
                        shape = RoundedCornerShape(24.dp),
                        shadowElevation = 14.dp,
                        glassColor = Color(0xF21E293B)
                    )
                    .padding(20.dp),
                color = Color.Transparent
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Catat Progress",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlassColors.TextPrimary
                        )

                        // Trash Bin delete action
                        IconButton(
                            onClick = {
                                viewModel.deleteBook(activeBook)
                                bookToUpdate = null
                            },
                            modifier = Modifier
                                .background(Color(0x3DF43F5E), shape = CircleShape)
                                .size(36.dp)
                                .testTag("delete_book_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Hapus Buku",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Text(
                        text = activeBook.title,
                        fontSize = 14.sp,
                        color = GlassColors.TextAccent,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                    Text(
                        text = "oleh ${activeBook.author}",
                        fontSize = 12.sp,
                        color = GlassColors.TextSecondary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Edit Fields
                    OutlinedTextField(
                        value = updateChapter,
                        onValueChange = { updateChapter = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("update_chapter_input"),
                        label = { Text("Bab Saat Ini (Max ${activeBook.totalChapters})", color = GlassColors.TextSecondary) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = GlassColors.TextPrimary,
                            unfocusedTextColor = GlassColors.TextPrimary,
                            focusedBorderColor = GlassColors.TextAccent,
                            unfocusedBorderColor = GlassColors.GlassBorderLight
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = updateNotes,
                        onValueChange = { updateNotes = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("update_notes_input"),
                        label = { Text("Catatan Membaca", color = GlassColors.TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = GlassColors.TextPrimary,
                            unfocusedTextColor = GlassColors.TextPrimary,
                            focusedBorderColor = GlassColors.TextAccent,
                            unfocusedBorderColor = GlassColors.GlassBorderLight
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Status Dropdown selector simulation
                    Text(
                        text = "Status Buku",
                        fontSize = 12.sp,
                        color = GlassColors.TextSecondary,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(bottom = 6.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val statuses = listOf("Reading" to "Sedang Dibaca", "Completed" to "Selesai", "Wishlist" to "Daftar Keinginan")
                        statuses.forEach { item ->
                            val isSelected = updateStatus == item.first
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Color(0x3106B6D4) else Color(0x0EFFFFFF))
                                    .clickable { updateStatus = item.first }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.second,
                                    color = if (isSelected) GlassColors.TextAccent else GlassColors.TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { bookToUpdate = null },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            elevation = null
                        ) {
                            Text("Batal", color = GlassColors.TextSecondary)
                        }

                        Button(
                            onClick = {
                                val chap = updateChapter.toIntOrNull() ?: activeBook.currentChapter
                                viewModel.updateBookProgress(
                                    book = activeBook,
                                    newChapter = chap,
                                    newNotes = updateNotes,
                                    newStatus = updateStatus
                                )
                                bookToUpdate = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(GlassColors.SoftIndigo, GlassColors.GlowViolet)
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 20.dp, vertical = 8.dp)
                            ) {
                                Text("Simpan", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LibraryBookItemRow(
    book: LibraryBookEntity,
    onUpdateProgressClicked: () -> Unit
) {
    val progress = if (book.totalChapters > 0) book.currentChapter.toFloat() / book.totalChapters else 0f
    val percentString = "${(progress * 100).toInt()}%"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .appleLiquidGlass(
                shape = RoundedCornerShape(18.dp),
                shadowElevation = 4.dp
            )
            .clickable { onUpdateProgressClicked() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Simple Book thumbnail / status logo
        Box(
            modifier = Modifier
                .size(60.dp, 80.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (book.status == "Completed")
                        Brush.verticalGradient(listOf(Color(0xFF34D399), Color(0xFF047857)))
                    else
                        Brush.verticalGradient(listOf(Color(0xFF818CF8), Color(0xFF4F46E5)))
                ),
            contentAlignment = Alignment.Center
        ) {
            if (book.coverId != null && book.coverId > 0) {
                AsyncImage(
                    model = "https://covers.openlibrary.org/b/id/${book.coverId}-M.jpg",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = if (book.status == "Completed") Icons.Default.CheckCircle else Icons.Default.Book,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Book details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = book.title,
                    color = GlassColors.TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Custom badge status style
                Box(
                    modifier = Modifier
                        .background(
                            color = when (book.status) {
                                "Completed" -> Color(0x2710B981)
                                "Reading" -> Color(0x278B5CF6)
                                else -> Color(0x22FFFFFF)
                            },
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = when (book.status) {
                            "Completed" -> "Selesai"
                            "Reading" -> "Sedang Baca"
                            else -> "Keinginan"
                        },
                        color = when (book.status) {
                            "Completed" -> GlassColors.GlowGreen
                            "Reading" -> GlassColors.GlowViolet
                            else -> GlassColors.TextSecondary
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Text(
                text = "oleh " + book.author,
                color = GlassColors.TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )

            if (book.notes.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notes,
                        contentDescription = "Catatan",
                        tint = GlassColors.TextAccent,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = book.notes,
                        color = GlassColors.TextSecondary.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Progress Bar with neat percentage badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (book.status == "Completed") GlassColors.GlowGreen else GlassColors.TextAccent,
                    trackColor = Color(0x1EFFFFFF)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "${book.currentChapter}/${book.totalChapters} Bab ($percentString)",
                    color = GlassColors.TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Small indicator icon for edit clickable
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit Progress",
            tint = GlassColors.TextSecondary.copy(alpha = 0.6f),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun StatBadge(
    title: String,
    value: String,
    badgeColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .appleLiquidGlass(
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 3.dp,
                glassColor = badgeColor
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = GlassColors.TextSecondary
        )
    }
}

@Composable
fun EmptyStateCard(
    onAddClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 30.dp)
            .appleLiquidGlass(
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 6.dp
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.MenuBook,
            contentDescription = null,
            tint = GlassColors.TextAccent.copy(alpha = 0.5f),
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Rak Bukumu Kosong",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = GlassColors.TextPrimary
        )
        Text(
            text = "Kamu belum memasukkan daftar target membaca pribadi. Masukkan buku favoritmu secara manual atau cari melalui rak buku tren!",
            fontSize = 12.sp,
            color = GlassColors.TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp, bottom = 18.dp)
        )
        Button(
            onClick = onAddClicked,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(GlassColors.SoftIndigo, GlassColors.GlowViolet)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Tambah Buku Pertama", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
