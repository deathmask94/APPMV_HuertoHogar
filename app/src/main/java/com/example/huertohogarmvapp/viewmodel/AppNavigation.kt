package com.example.huertohogarmvapp.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.huertohogarmvapp.pages.ProductDetailsPage
import com.example.huertohogarmvapp.view.AuthScreen
import com.example.huertohogarmvapp.view.HomeScreen
import com.example.huertohogarmvapp.view.LoginScreen
import com.example.huertohogarmvapp.view.SignupScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
@Composable
fun AppNavigation(modifier: Modifier = Modifier){

    val navController = rememberNavController()
    GlobalNavigation.navController = navController

    val isLoggedIn = Firebase.auth.currentUser!=null
    val firstPage  = if(isLoggedIn) "home" else "auth"

    NavHost(navController = navController, startDestination = firstPage) {

        composable("auth"){
            AuthScreen(modifier, navController)
        }

        composable("login"){
            LoginScreen(modifier,navController)
        }

        composable("signup"){
            SignupScreen(modifier,navController)
        }

        composable ("home"){
            HomeScreen(modifier,navController)
        }

        composable ("product-details/{productId}"){
            var productId = it.arguments?.getString("productId")
            ProductDetailsPage(modifier,productId?:"")
        }


    }

}

object GlobalNavigation{
    lateinit var navController: NavHostController
}
