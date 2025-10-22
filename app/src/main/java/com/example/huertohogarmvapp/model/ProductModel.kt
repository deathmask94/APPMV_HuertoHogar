package com.example.huertohogarmvapp.model

data class ProductModel(
    val id : String = "",
    val title : String = "",
    val description : String = "",
    val price : String = "",
    val category : String = "",
    val images : List<String> = emptyList()
)
