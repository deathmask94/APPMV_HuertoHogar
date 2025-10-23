package com.example.huertohogarmvapp

import android.content.Context
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue

object AppUtil {

    fun showToast(context: Context,message : String){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show()
    }

    fun addItemToCart(productId: String,context: Context){

        val userDoc = Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)


        userDoc.get().addOnCompleteListener {
            if (it.isSuccessful){
                val currentCart = it.result.get("cartItems") as? Map<String,Long> ?: emptyMap()
                val currentQuantity = currentCart[productId]?:0
                val updatedQuantity = currentQuantity + 1;

                val updatedCart = mapOf("cartItems.$productId" to updatedQuantity)

                userDoc.update(updatedCart)
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            showToast(context,"Añadido al carrito")
                        }else{
                            showToast(context,"No es posible añadir :(")
                        }
                    }
            }
        }


    }


    fun removeFromCart(productId: String,context: Context,removeAll: Boolean = false){

        val userDoc = Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)


        userDoc.get().addOnCompleteListener {
            if (it.isSuccessful){
                val currentCart = it.result.get("cartItems") as? Map<String,Long> ?: emptyMap()
                val currentQuantity = currentCart[productId]?:0
                val updatedQuantity = currentQuantity - 1;



                val updatedCart =
                    if (updatedQuantity<=0 || removeAll)
                        mapOf("cartItems.$productId" to FieldValue.delete())
                    else
                        mapOf("cartItems.$productId" to updatedQuantity)

                userDoc.update(updatedCart)
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            showToast(context,"Producto removido")
                        }else{
                            showToast(context,"No es posible remover :(")
                        }
                    }
            }
        }


    }




}