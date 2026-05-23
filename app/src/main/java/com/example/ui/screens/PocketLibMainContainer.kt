package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassColors
import com.example.ui.theme.appleLiquidGlass
import com.example.ui.viewmodel.PocketLibViewModel

@Composable
fun PocketLibMainContainer(
    viewModel: PocketLibViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(GlassColors.MagicAuraGradient),
        containerColor = Color.Transparent,
        topBar = {
            // Elegant modern glass top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .appleLiquidGlass(
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 4.dp
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Brand Logo and Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "PocketLib logo",
                        tint = GlassColors.TextAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PocketLib",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                // Right side: Active user and Log Out icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .background(Color(0x1AFFFFFF), shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = GlassColors.TextAccent,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = currentUser?.username ?: "Guest",
                            color = GlassColors.TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Logout trigger
                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier
                            .size(30.dp)
                            .background(Color(0x1DF43F5E), shape = CircleShape)
                            .testTag("logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Sign out",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            // Apple Liquid Glass Custom Bottom Navigation Bar
            // Adheres strictly to notches and gesture navigation pads
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(start = 24.dp, end = 24.dp, bottom = 12.dp)
                    .appleLiquidGlass(
                        shape = RoundedCornerShape(24.dp),
                        shadowElevation = 10.dp
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // TAB 1: Popular Books Explore
                    BottomNavItem(
                        label = "Katalog",
                        icon = Icons.Default.Explore,
                        isSelected = currentTab == "popular",
                        onClick = { viewModel.selectTab("popular") },
                        modifier = Modifier.testTag("nav_tab_explore")
                    )

                    // TAB 2: Personal target/reading logging SQLite
                    BottomNavItem(
                        label = "Koleksiku",
                        icon = Icons.Default.Book,
                        isSelected = currentTab == "library",
                        onClick = { viewModel.selectTab("library") },
                        modifier = Modifier.testTag("nav_tab_library")
                    )
                }
            }
        }
    ) { innerPadding ->
        // Render current selected tab screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {
            if (currentTab == "popular") {
                PopularBooksScreen(viewModel = viewModel)
            } else {
                LibraryScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun BottomNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeColor = GlassColors.TextAccent
    val idleColor = GlassColors.TextSecondary

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) activeColor else idleColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isSelected) GlassColors.TextPrimary else idleColor,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
