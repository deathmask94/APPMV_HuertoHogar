package com.example.huertohogarmvapp.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.huertohogarmvapp.components.BannerView
import com.example.huertohogarmvapp.components.HeaderView
import com.example.huertohogarmvapp.model.ProductModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import androidx.compose.foundation.lazy.LazyRow
import com.example.huertohogarmvapp.components.ProductItemView

@Composable
fun HomePage (modifier: Modifier = Modifier){

    val productsList = remember {
        mutableStateOf<List<ProductModel>>(emptyList())
    }

    LaunchedEffect(key1 = Unit) {
        Firebase.firestore.collection("data").document("stock")
            .collection("products")
            .get().addOnCompleteListener() {
                if(it.isSuccessful) {
                    val resulList = it.result.documents.mapNotNull { doc ->
                        doc.toObject(ProductModel::class.java)
                    }
                    productsList.value = resulList.plus(resulList).plus(resulList)
                }
            }
    }

    Column(
        modifier = modifier.fillMaxWidth()
            .padding(17.dp)
    ) {
        HeaderView(modifier)
        BannerView(modifier)


        Spacer(modifier = Modifier.height(50.dp))

        LazyColumn {
            items(productsList.value.chunked(2)){rowItems ->
                Row {
                    rowItems.forEach {
                        ProductItemView(product = it, modifier = Modifier.weight(1f))
                    }
                    if(rowItems.size==1){
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }

















}