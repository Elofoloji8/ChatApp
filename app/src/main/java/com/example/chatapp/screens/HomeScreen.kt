package com.example.chatapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chatapp.R
import com.example.chatapp.models.Database
import com.example.chatapp.navigations.Routes

@Composable
fun HomeScreen(navController: NavController, database: Database){

    var expanded by remember { mutableStateOf(false) }
    val authState = database.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is Database.AuthState.Unauthenticated -> navController.navigate(Routes.screenLogin)
            // Diğer durumlar için hiçbir şey yapma
            else -> Unit
        }
    }

    // Kullanıcının e-posta adresini al
    val currentUserEmail = database.getCurrentUserEmail()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color(0xFF38B6FF))
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
                            // "Admin panel" seçeneğine tıklandığında yapılacak işlemler
                            expanded = false
                            navController.navigate(Routes.screenAdminPanel)
                        }
                    )
                }
                DropdownMenuItem( text = { Text("Çıkış Yap") },
                    onClick = {
                        // "Çıkış Yap" seçeneğine tıklandığında yapılacak işlemler
                        expanded = false
                        database.signout()
                    }
                )
            }
        }



    }
}

@Composable
fun IconComponent(drawableId: Int){
    Icon(
        painter = painterResource(id = drawableId),
        contentDescription = "",
        tint = Color.White
    )
}