package com.example.huertohogarmvapp.model

data class UserModel(
    val name : String = "" ,
    val email : String = "",
    val udi : String = "",
    val cartItems : Map<String,Long> = emptyMap(),
    val address : String = "",
)
