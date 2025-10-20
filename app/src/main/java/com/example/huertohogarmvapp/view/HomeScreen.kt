package com.example.huertohogarmvapp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.material.Scaffold
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
@Composable
fun HomeScreen(modifier : Modifier = Modifier,navController: NavController) {
    Scaffold {
        ContentScreen(modifier = modifier.padding(it))
    }
}

@Composable
fun ContentScreen (modifier: Modifier = Modifier){

}