package com.example.chatapp.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.chatapp.models.Database


@Composable
fun AdminPanelScreen(navController: NavController, database: Database) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var selectedRole by remember { mutableStateOf("Rol") }
    var selectedArea by remember {mutableStateOf("Alan")}
    var expandedRole by remember { mutableStateOf(false) } // Dropdown'un açık olup olmadığını kontrol eder
    var expandedArea by remember { mutableStateOf(false) }

    val roles = listOf("Proje Yöneticisi", "Asistan", "Çalışan") // Dropdown menü seçenekleri
    val areas = listOf("Web", "Mobil", "Yapay zeka", "Veri tabanı")
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0XFFFFFFFF))
    ) {

        Spacer(modifier = Modifier.size(100.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Kullanıcı Adı") },
            modifier = Modifier
                .fillMaxWidth()
                .border(0.dp, Color.Transparent)
                .background(Color.White)
                .padding(20.dp),
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Şifre") },
            modifier = Modifier
                .fillMaxWidth()
                .border(0.dp, Color.Transparent)
                .background(Color.White)
                .padding(20.dp),
        )

        Spacer(modifier = Modifier.size(20.dp))

        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable { expandedRole= true } // Tıklanabilir alan
            .border(1.dp, Color.Gray) // İsteğe bağlı olarak bir sınır ekleyebilirsiniz
            .padding(15.dp)
        ) {
            Text(text = selectedRole)

            DropdownMenu(
                expanded = expandedRole,
                onDismissRequest = { expandedRole = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                roles.forEach { role ->
                    DropdownMenuItem(
                        text = { Text(role) },
                        onClick = {
                            selectedRole = role
                            expandedRole = false
                        },
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.size(20.dp))

        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable { expandedArea = true } // Tıklanabilir alan
            .border(1.dp, Color.Gray) // İsteğe bağlı olarak bir sınır ekleyebilirsiniz
            .padding(15.dp)
        ) {
            Text(text = selectedArea)

            DropdownMenu(
                expanded = expandedArea,
                onDismissRequest = { expandedArea = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                areas.forEach { area ->
                    DropdownMenuItem(
                        text = { Text(area) },
                        onClick = {
                            selectedArea = area
                            expandedArea = false
                        },
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }


        Spacer(modifier = Modifier.size(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 130.dp)
                .height(40.dp),
        ) {
            Button(
                onClick = {
                    database.registerPerson(username, password, selectedRole, selectedArea, context)
                          println("kullanıcı adı: $username sifre: $password  rol: $selectedRole alan: $selectedArea")
                },
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
                    text = "Kaydet",
                    style = TextStyle(fontSize = 16.sp),
                    color = Color(0xFF38B6FF)
                )
            }
        }
        





    }


}