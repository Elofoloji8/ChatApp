package com.example.chatapp.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chatapp.R
import com.example.chatapp.models.Database
import com.example.chatapp.navigations.NavItem
import com.example.chatapp.navigations.Routes

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController, database: Database) {
    var expanded by remember { mutableStateOf(false) }
    val authState = database.authState.observeAsState()
    val currentUserEmail = database.getCurrentUserEmail()

    // Navigation bar item listesi
    val items: List<NavItem> = listOf(
        NavItem(
            title = "Chats",
            selectedIcon = ImageVector.vectorResource(id = R.drawable.filled_chat_icon),
            unSelectedIcon = ImageVector.vectorResource(id = R.drawable.outline_chat_icon)
        ),
        NavItem(
            title = "Profile",
            selectedIcon = Icons.Filled.Person,
            unSelectedIcon = Icons.Outlined.Person
        )
    )

    var selectedIndex by remember { mutableStateOf(0) }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is Database.AuthState.Unauthenticated -> navController.navigate(Routes.screenLogin)
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color(0xFF778899))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ChatHub",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                )

                Spacer(modifier = Modifier.weight(1f))
                IconComponent(drawableId = R.drawable.search_icon)
                Spacer(modifier = Modifier.size(20.dp))
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.more_icon),
                        contentDescription = null,
                        tint = Color(0XFFFFFFFF)
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        offset = DpOffset(x = 24.dp / 2, y = 24.dp)
                    ) {
                        if (currentUserEmail == "admin@gmail.com") {
                            DropdownMenuItem(
                                text = { Text("Admin panel") },
                                onClick = {
                                    expanded = false
                                    navController.navigate(Routes.screenAdminPanel)
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Çıkış Yap") },
                            onClick = {
                                expanded = false
                                database.signOut()
                            }
                        )
                    }
                }
            }
        },
        content = {
            when (selectedIndex) {
                1 -> ProfileScreen()
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Routes.screenContacts)
            },
                containerColor = Color(0xFF2F4F4F),
                contentColor = Color.White
            ) {
                // Animasyonlu buton
                val scale = animateFloatAsState(if (selectedIndex == 1) 1.2f else 1f)
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "",
                    modifier = Modifier.scale(scale.value)
                )
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.background(Color.Transparent),
                containerColor = Color(0xFF778899)
            ) {
                items.forEachIndexed { index, navItem ->
                    val isSelected = selectedIndex == index
                    val rippleEffectColor = if (isSelected)  Color(0xFF778899) else Color.White
                    val iconSize by animateFloatAsState(if (isSelected) 1.2f else 1f)

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedIndex = index },
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(48.dp) // Dairenin büyüklüğünü ayarla
                                    .background(
                                        color = if (isSelected) rippleEffectColor.copy(alpha = 0.2f) else Color.Transparent, // Seçili olan için hafif renk
                                        shape = CircleShape // Yuvarlak şekil
                                    )
                                    .padding(8.dp), // İkon ile daire arasına boşluk ekle
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isSelected) navItem.selectedIcon else navItem.unSelectedIcon,
                                    contentDescription = null,
                                    modifier = Modifier.size(30.dp), // İkon büyüklüğünü sabit tut
                                    tint = rippleEffectColor
                                )
                            }
                        },
                        label = {
                            Text(
                                text = navItem.title,
                                fontSize = 12.sp,
                                color = rippleEffectColor
                            )
                        },
                        modifier = Modifier.clickable { selectedIndex = index } // Ripple efekti
                    )
                }
            }
        }
    )
}

@Composable
fun IconComponent(drawableId: Int) {
    Icon(
        painter = painterResource(id = drawableId),
        contentDescription = "",
        tint = Color.White
    )
}

