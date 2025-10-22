package com.example.huertohogarmvapp.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.huertohogarmvapp.model.ProductModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
@Composable
fun ProductDetailsPage(modifier: Modifier = Modifier,productId : String) {


    var product by remember {
        mutableStateOf(ProductModel())
    }

    LaunchedEffect(key1 = Unit) {
        Firebase.firestore.collection("data").document("stock")
            .collection("products")
            .document(productId).get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    var result = it.result.toObject(ProductModel::class.java)
                    if (result!=null){
                        product = result
                    }
                }
            }

    }



    Column (
        modifier = modifier.fillMaxSize()
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = product.title,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(50.dp))

        val imageUrl = product.images.firstOrNull()
        if (!imageUrl.isNullOrBlank()) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "Imagen del producto",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(bottom = 12.dp),
                contentScale = ContentScale.Crop
            )
        }



        /*
        Image(
            painter = rememberAsyncImagePainter(product.images.firstOrNull()),
            contentDescription = "Imagen del producto",
            modifier = Modifier
                .fillMaxSize() // puedes ajustar si quieres que ocupe menos espacio
                .padding(8.dp),
            contentScale = ContentScale.Crop
        )

        */

    }




}