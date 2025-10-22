package com.example.huertohogarmvapp.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle

@Composable
fun HeaderView(modifier: Modifier = Modifier) {

    var name by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)
            .get().addOnCompleteListener(){
                name = it.result.get("name").toString().split(" ").get(0)
            }

    }

    Column {
        Text(text = "Bienvenido Otra vez !! ")
        Spacer(modifier = Modifier.height(17.dp))
        Text(text = name, style = TextStyle(
             fontSize = 18.sp,
             fontWeight = FontWeight.Bold
        ))
    }
}