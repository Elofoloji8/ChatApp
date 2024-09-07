package com.example.chatapp.screens

import android.graphics.Paint.Style
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.example.chatapp.R
import com.example.chatapp.data.UserProfile
import com.example.chatapp.models.Database
import com.example.chatapp.navigations.Routes

@Composable
fun ContactsScreen(navController: NavController,database: Database) {
    var userList by remember { mutableStateOf(emptyList<UserProfile>()) }
    var expanded by remember { mutableStateOf(false) }
    val currentUserEmail = database.getCurrentUserEmail()

    // Tüm kullanıcı profillerini çekme
    LaunchedEffect(Unit) {
        database.getAllUserProfile { users ->
            userList = users
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color(0xFF778899))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "Contacts",
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
                        database.signOut()
                    }
                )
            }
        }
    }



    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.size(64.dp))
        // Kullanıcı listesini göster
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            items(userList) { user ->
                UserProfileItem(user = user)
            }
        }
    }
}

@Composable
fun UserProfileItem(user: UserProfile) {
    val context = LocalContext.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Profil Resmi - Yuvarlak Şekilde
        Image(
            painter =  rememberAsyncImagePainter(
                model = ImageRequest.Builder(context)
                    .data(user.profileImageUri)
                    .transformations(CircleCropTransformation())
                    .build()
                ),
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape) // Yuvarlak şekil
                .padding(8.dp),
            contentScale = ContentScale.Crop
        )

        // Kullanıcı Adı
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f)
        ) {
            Text(
                text = user.name,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = user.about,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color.Gray
                ),
                maxLines = 2, // İsterseniz satır sınırını belirleyebilirsiniz
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}