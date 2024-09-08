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

import androidx.compose.ui.text.input.PasswordVisualTransformation

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
        targetValue = if (isLoggedIn) 120.dp else 0.dp,
        animationSpec = tween(durationMillis = 300)
    )

    val authState = database.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when(authState.value){
            is Database.AuthState.Authenticated -> navController.navigate(Routes.screenHome)
            is Database.AuthState.Error -> Toast.makeText(context,
                (authState.value as Database.AuthState.Error).message, Toast.LENGTH_SHORT).show()
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
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "",
            tint = Color(0xFF778899),
            modifier = Modifier
                .size(240.dp)
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
                        database.signIn(username,password)
                    } else {
                        isLoggedIn = true
                    } },
                modifier = Modifier
                    .fillMaxSize(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFCBCBCB),
                    contentColor = Color(0xFF778899)
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(2.dp, Color(0xFF778899)),
                elevation = ButtonDefaults.buttonElevation(4.dp) // Butona gölge ekleme
            ) {
                Text(
                    text = "Giriş yap",
                    style = TextStyle(fontSize = 16.sp),
                    color = Color.Black
                )
            }
        }

        AnimatedVisibility(
            visible = !isLoggedIn,
            enter = fadeIn(tween(durationMillis = 300)),
            exit = fadeOut(tween(durationMillis = 300))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(220.dp))

                Text(
                    text = "from",
                    style = TextStyle(fontSize = 16.sp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "yazilim.xyz",
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.alex_brush)),
                        fontSize = 30.sp
                    )
                )
            }
        }

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
                Spacer(modifier = Modifier.height(100.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Kullanıcı Adı") },
                    placeholder = { Text("Kullanıcı adınızı girin") }, // Placeholder ekledim
                    leadingIcon = { // İkon ekleme
                        Icon(
                            painter = painterResource(id = R.drawable.user_icon),
                            contentDescription = "Kullanıcı İkonu"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.dp, Color.Transparent)
                        .background(Color.White),
                    shape = RoundedCornerShape(10.dp), // Yuvarlatılmış köşeler
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF778899), // Odaklanıldığında renk değişimi
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(15.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Şifre") },
                    placeholder = { Text("Şifrenizi girin") }, // Placeholder ekledim
                    leadingIcon = { // İkon ekleme
                        Icon(
                            painter = painterResource(id = R.drawable.pswrd_icon),
                            contentDescription = "Şifre İkonu"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.dp, Color.Transparent)
                        .background(Color.White),
                    visualTransformation = PasswordVisualTransformation(), // Şifre gizleme
                    shape = RoundedCornerShape(10.dp), // Yuvarlatılmış köşeler
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF778899), // Odaklanıldığında renk değişimi
                        unfocusedBorderColor = Color.Gray
                    )
                )
            }
        }
    }

}