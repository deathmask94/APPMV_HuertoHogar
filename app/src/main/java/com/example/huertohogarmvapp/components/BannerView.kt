package com.example.huertohogarmvapp.components

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import coil.compose.AsyncImage


@Composable
fun BannerView (modifier : Modifier = Modifier){

    var bannerList by remember{
        mutableStateOf<List<String>>(emptyList())
    }

    LaunchedEffect(Unit) {
        Firebase.firestore.collection("data")
            .document("banners")
            .get().addOnCompleteListener(){
                bannerList = it.result.get("urls") as List<String>

            }
    }

    Spacer(modifier = Modifier.height(20.dp))

    AsyncImage(model = if(bannerList.size>0) bannerList.get(0) else "",
               contentDescription = "banner",
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(17.dp))
                )
}