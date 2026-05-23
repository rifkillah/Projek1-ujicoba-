package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.Work
import com.example.ui.theme.GlassColors
import com.example.ui.theme.appleLiquidGlass
import com.example.ui.viewmodel.PocketLibViewModel
import com.example.ui.viewmodel.PopularBooksState

@Composable
fun PopularBooksScreen(
    viewModel: PocketLibViewModel,
    modifier: Modifier = Modifier
) {
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val popularBooksState by viewModel.popularBooksState.collectAsState()

    // Dialog trigger for adding books to SQLite straight from API
    var bookToAdd by remember { mutableStateOf<Work?>(null) }
    var totalChaptersText by remember { mutableStateOf("12") }
    var currentChapterText by remember { mutableStateOf("0") }
    var notesText by remember { mutableStateOf("") }

    // Subjects listing
    val subjects = listOf(
        SubjectItem("classic", "Klasik", Icons.Default.MenuBook),
        SubjectItem("fantasy", "Fantasi", Icons.Default.Star),
        SubjectItem("mystery", "Detektif", Icons.Default.Psychology),
        SubjectItem("history", "Sejarah", Icons.Default.History),
        SubjectItem("science", "Sains", Icons.Default.Science)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Screen description
        Text(
            text = "Eksplorasi Tren",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = GlassColors.TextPrimary
        )
        Text(
            text = "Temukan buku populer dunia langsung dari OpenLibrary API",
            fontSize = 13.sp,
            color = GlassColors.TextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // Shifting row of gorgeous glassy genre pills
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(subjects) { subject ->
                val isSelected = selectedSubject == subject.key
                val glassBgColor = if (isSelected) Color(0x3DFFFFFF) else GlassColors.WhiteGlassSecondary
                val borderHighlight = if (isSelected) Color(0x99FFFFFF) else GlassColors.GlassBorderDim

                Row(
                    modifier = Modifier
                        .height(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(glassBgColor)
                        .clickable { viewModel.selectSubject(subject.key) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = subject.icon,
                        contentDescription = subject.label,
                        tint = if (isSelected) GlassColors.TextAccent else GlassColors.TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = subject.label,
                        color = if (isSelected) GlassColors.TextPrimary else GlassColors.TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // State Machine for list content
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (val state = popularBooksState) {
                is PopularBooksState.Loading -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = GlassColors.TextAccent)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Menghubungkan ke rak buku...",
                            color = GlassColors.TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
                is PopularBooksState.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = "Error Connection icon",
                            tint = GlassColors.CoralPeach,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = state.message,
                            color = GlassColors.TextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Button(
                            onClick = { viewModel.fetchPopularBooks(selectedSubject) },
                            colors = ButtonDefaults.buttonColors(containerColor = GlassColors.WhiteGlassBackground),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Retry", tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Coba Lagi", color = Color.White)
                        }
                    }
                }
                is PopularBooksState.Success -> {
                    if (state.books.isEmpty()) {
                        Text("Tidak ada buku terpopuler hari ini.", color = GlassColors.TextSecondary)
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(bottom = 80.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.books) { work ->
                                PopularBookCard(
                                    work = work,
                                    onAddClicked = {
                                        bookToAdd = work
                                        totalChaptersText = "15"
                                        currentChapterText = "0"
                                        notesText = ""
                                    }
                                )
                            }
                        }
                    }
                }
                else -> {
                    // Idle state
                    Text("Pilih genre buku terlebih dahulu.", color = GlassColors.TextSecondary)
                }
            }
        }
    }

    // Interactive add dialog straight from the catalog list
    if (bookToAdd != null) {
        val selectedWork = bookToAdd!!
        val authorName = selectedWork.authors?.firstOrNull()?.name ?: "Penulis Anonim"

        Dialog(onDismissRequest = { bookToAdd = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .appleLiquidGlass(
                        shape = RoundedCornerShape(24.dp),
                        shadowElevation = 14.dp,
                        glassColor = Color(0xF21E293B) // slightly opaque slate for popup crisp readability
                    )
                    .padding(20.dp),
                color = Color.Transparent
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Tambah ke Perpustakaanku",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlassColors.TextPrimary
                    )
                    Text(
                        text = selectedWork.title,
                        fontSize = 14.sp,
                        color = GlassColors.TextAccent,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text(
                        text = "oleh $authorName",
                        fontSize = 12.sp,
                        color = GlassColors.TextSecondary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Form inputs
                    OutlinedTextField(
                        value = totalChaptersText,
                        onValueChange = { totalChaptersText = it },
                        label = { Text("Total Bab Buku", color = GlassColors.TextSecondary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = GlassColors.TextPrimary,
                            unfocusedTextColor = GlassColors.TextPrimary,
                            focusedBorderColor = GlassColors.TextAccent,
                            unfocusedBorderColor = GlassColors.GlassBorderLight
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_dialog_total_chapters_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = currentChapterText,
                        onValueChange = { currentChapterText = it },
                        label = { Text("Sudah Membaca Bab", color = GlassColors.TextSecondary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = GlassColors.TextPrimary,
                            unfocusedTextColor = GlassColors.TextPrimary,
                            focusedBorderColor = GlassColors.TextAccent,
                            unfocusedBorderColor = GlassColors.GlassBorderLight
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_dialog_current_chapter_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        label = { Text("Catatan / Kesan Pertama", color = GlassColors.TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = GlassColors.TextPrimary,
                            unfocusedTextColor = GlassColors.TextPrimary,
                            focusedBorderColor = GlassColors.TextAccent,
                            unfocusedBorderColor = GlassColors.GlassBorderLight
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_dialog_notes_input")
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { bookToAdd = null },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            elevation = null
                        ) {
                            Text("Batal", color = GlassColors.CoralPeach)
                        }

                        Button(
                            onClick = {
                                val tot = totalChaptersText.toIntOrNull() ?: 12
                                val cur = currentChapterText.toIntOrNull() ?: 0
                                viewModel.addBook(
                                    title = selectedWork.title,
                                    author = authorName,
                                    totalChapters = tot,
                                    currentChapter = cur,
                                    notes = notesText,
                                    coverId = selectedWork.coverId
                                )
                                bookToAdd = null
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
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
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
fun PopularBookCard(
    work: Work,
    onAddClicked: () -> Unit
) {
    val context = LocalContext.current
    val coverUrl = work.getCoverUrl()
    val author = work.authors?.firstOrNull()?.name ?: "Penulis Anonim"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .appleLiquidGlass(
                shape = RoundedCornerShape(20.dp),
                shadowElevation = 4.dp
            )
            .padding(10.dp)
    ) {
        // Book Cover Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.72f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x1F2C3E50))
        ) {
            if (coverUrl != null) {
                AsyncImage(
                    model = coverUrl,
                    contentDescription = "Cover for ${work.title}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Fallback elegant cover
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(GlassColors.DeepPurple, GlassColors.GlowViolet)
                            )
                        )
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = "No cover placeholder",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = work.title,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Title
        Text(
            text = work.title,
            color = GlassColors.TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Author
        Text(
            text = author,
            color = GlassColors.TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 2.dp)
        )

        // Publish Year
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (work.firstPublishYear != null) "${work.firstPublishYear}" else "N/A",
                color = GlassColors.TextAccent,
                fontSize = 10.sp,
                fontWeight = FontWeight.Light
            )

            // Transparent Bookmark action
            IconButton(
                onClick = onAddClicked,
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0x22FFFFFF), shape = RoundedCornerShape(8.dp))
                    .testTag("add_book_api_button")
            ) {
                Icon(
                    imageVector = Icons.Default.BookmarkAdd,
                    contentDescription = "Add Book To Library",
                    tint = GlassColors.TextPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

data class SubjectItem(val key: String, val label: String, val icon: ImageVector)
