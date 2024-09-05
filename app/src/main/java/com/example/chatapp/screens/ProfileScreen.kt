package com.example.chatapp.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.example.chatapp.R
import com.example.chatapp.models.Database
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


@Composable
fun ProfileScreen() {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var userName by remember { mutableStateOf("") }  // Başlangıçta boş
    var aboutMe by remember { mutableStateOf("") }   // Başlangıçta boş
    var isEditingName by remember { mutableStateOf(false) }
    var isEditingAbout by remember { mutableStateOf(false) }
    var userId by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser?.uid ?: "") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val coroutineScope = rememberCoroutineScope()
    val database = Database()


    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            database.getUserProfile(userId) { name, about, imageUri ->
                if (imageUri != null) {
                    Log.e("SATURN", imageUri.toString())
                    selectedImageUri = Uri.parse(imageUri.toString()) // Resmi tekrar yükle
                } else {
                    Log.e("SATURN", "No profile image found")
                }
                userName = name
                aboutMe = about
            }
        }
    }


    // Kullanıcı profilini kaydetme fonksiyonu
    fun saveProfile() {
        coroutineScope.launch {
            println(selectedImageUri)
            // Profil resmini URI olarak saklayabilir, Firebase Storage'dan indirme işlemi yapabilirsiniz.
            val imageUriString = selectedImageUri?.toString()
            // Profil bilgilerini kaydet
            database.saveUserProfile(userName, aboutMe, imageUriString)
        }
    }

    LaunchedEffect(selectedImageUri) {
        if(selectedImageUri != null){
            saveProfile()
        }
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.size(72.dp))

        Box(
            modifier = Modifier
                .size(150.dp)
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            if (selectedImageUri != null) {
                val context = LocalContext.current
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(context)
                        .data(selectedImageUri)
                        .transformations(CircleCropTransformation())
                        .build()
                )
                Image(
                    painter = painter,
                    contentDescription = "Profil Resmi",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.image_pick_icon),
                    contentDescription = "Varsayılan Profil Resmi",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            IconButton(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF38B6FF))
                    .align(Alignment.BottomEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Resim Yükle",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isEditingName) {
                    TextField(
                        value = userName,
                        onValueChange = { userName = it },
                        label = { Text("Adınız") },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Text(
                        text = if (userName.isNotEmpty()) userName else "Adınız",
                        modifier = Modifier.weight(1f)
                    )
                }

                IconButton(onClick = {
                    if (isEditingName) saveProfile()  // Kaydet butonuna tıklayınca veriyi kaydet
                    isEditingName = !isEditingName
                }) {
                    Icon(
                        imageVector = if (isEditingName) Icons.Default.Save else Icons.Default.Edit,
                        contentDescription = if (isEditingName) "Kaydet" else "Düzenle"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isEditingAbout) {
                    TextField(
                        value = aboutMe,
                        onValueChange = { aboutMe = it },
                        label = { Text("Hakkımda") },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Text(
                        text = if (aboutMe.isNotEmpty()) aboutMe else "Hakkımda",
                        modifier = Modifier.weight(1f)
                    )
                }

                IconButton(onClick = {
                    if (isEditingAbout) saveProfile()  // Kaydet butonuna tıklayınca veriyi kaydet
                    isEditingAbout = !isEditingAbout
                }) {
                    Icon(
                        imageVector = if (isEditingAbout) Icons.Default.Save else Icons.Default.Edit,
                        contentDescription = if (isEditingAbout) "Kaydet" else "Düzenle"
                    )
                }
            }
        }
    }
}