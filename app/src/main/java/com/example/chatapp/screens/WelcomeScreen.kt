package com.example.chatapp.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.chatapp.R
import com.example.chatapp.navigations.Routes

@Composable
fun WelcomeScreen(navController: NavController){

    var scale by remember { mutableStateOf(1f) }

    val animatedScale by animateFloatAsState(
        targetValue = scale,
        //Düzeltilecek
        animationSpec = tween(durationMillis = 1000)
    )

    Column( modifier = Modifier.
        fillMaxSize()
        .background(Color(0xFF778899)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,

        ) {

        Icon(
            painter = painterResource(id = R.drawable.welcome_icon),
            contentDescription = "",
            tint = Color.White,
            modifier = Modifier.graphicsLayer(
                scaleX = animatedScale,
                scaleY = animatedScale
            )
        )

    }

    LaunchedEffect(animatedScale) {
        if (animatedScale == 2f) {
            navController.navigate(Routes.screenLogin)
        }
    }


    LaunchedEffect(Unit) {
        scale = 2f // İkon 2 kat büyür
    }
}