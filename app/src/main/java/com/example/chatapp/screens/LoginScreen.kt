package com.example.chatapp.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chatapp.R
import com.example.chatapp.models.Database
import com.example.chatapp.navigations.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, database : Database){

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoggedIn by remember { mutableStateOf(false) }

    val iconOffset by animateDpAsState(
        targetValue = if (isLoggedIn) (-100).dp else 0.dp,
        animationSpec = tween(durationMillis = 300)
    )

    val buttonOffset by animateDpAsState(
        targetValue = if (isLoggedIn) 80.dp else 0.dp,
        animationSpec = tween(durationMillis = 300)
    )

    // Kullanıcının giriş doğrulama sürecini tutar
    val authState = database.authState.observeAsState()
    // Mevcut ortamı alır
    val context = LocalContext.current

    // Kullanıcının giriş durumuna göre işlem yapar. Eğer giriş başarılıysa kullanıcı home ekranına yönlendirilir.
    // Eğer giriş başarısızsa ekrana kısa bir süreliğine toast mesajı şeklinde neden başarısız olduğunu anlatan bir mesaj görünür
    LaunchedEffect(authState.value) {
        when(authState.value){
            is Database.AuthState.Authenticated -> navController.navigate(Routes.screenHome)
            is Database.AuthState.Error -> Toast.makeText(context,
                (authState.value as Database.AuthState.Error).message, Toast.LENGTH_SHORT).show()
            // Diğer durumlar için hiçbir şey yapma
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(156.dp))

        Icon(
            painter = painterResource(id = R.drawable.welcome_icon),
            contentDescription = "",
            tint = Color(0xFF38B6FF),
            modifier = Modifier
                .size(256.dp)
                .offset(y = iconOffset)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 130.dp)
                .height(40.dp)
                .offset(y = buttonOffset),
        ) {
            Button(
                onClick = {
                    if (isLoggedIn) {
                        database.signin(username,password)
                    } else {
                        isLoggedIn = true
                    } },
                modifier = Modifier
                    .fillMaxSize(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEBEBEB),
                    contentColor = Color(0xFF38B6FF)
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF38B6FF)),
                elevation = null
            ) {
                Text(
                    text = "Giriş yap",
                    style = TextStyle(fontSize = 16.sp),
                    color = Color(0xFF38B6FF)
                )
            }
        }

        // TextView'ların ve butonun kaybolmasını sağlamak için AnimatedVisibility kullanılır
        AnimatedVisibility(
            visible = !isLoggedIn,
            enter = fadeIn(tween(durationMillis = 300)),
            exit = fadeOut(tween(durationMillis = 300))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(128.dp))

                Text(
                    text = "from",
                    style = TextStyle(fontSize = 16.sp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "yazilim.xyz",
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.alex_brush)),
                        fontSize = 32.sp
                    )
                )
            }
        }

        // TextField'ların görünür olması
        AnimatedVisibility(
            visible = isLoggedIn,
            enter = fadeIn(tween(durationMillis = 300)),
            exit = fadeOut(tween(durationMillis = 300))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.End)
                    .offset(y = (-225).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(50.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Kullanıcı Adı") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.dp, Color.Transparent)
                        .background(Color.White),
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Şifre") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.dp, Color.Transparent)
                        .background(Color.White),
                )
            }
        }
    }

}