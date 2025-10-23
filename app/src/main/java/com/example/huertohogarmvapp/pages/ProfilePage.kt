package com.example.huertohogarmvapp.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.huertohogarmvapp.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.R.attr.fontWeight
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.res.painterResource

import com.example.huertohogarmvapp.R
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextField
import androidx.navigation.NavHostController


@Composable
fun ProfilePage (modifier: Modifier = Modifier){

    val userModel = remember {
        mutableStateOf(UserModel())
    }

    var addressInput by remember {
        mutableStateOf(userModel.value.address)
    }


    LaunchedEffect(key1 = Unit) {
        Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val result = it.result.toObject(UserModel::class.java)
                    if (result != null){
                        userModel.value = result
                        addressInput = userModel.value.address
                    }
                }
            }
    }



    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(17.dp),
    ) {
        Text(
            text = ("Tu Perfil"), style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(25.dp))

        Image(
            painter = painterResource(id = R.drawable.profile),
            contentDescription = "fotoperfil",
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = userModel.value.name,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
            )


        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "Direccion: ",
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
        )

        Text(text = userModel.value.address)

    }
}
