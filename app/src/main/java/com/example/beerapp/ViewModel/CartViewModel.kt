package com.example.beerapp.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.beerapp.activities.adapter.Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CartViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val _cartItems = MutableLiveData<MutableList<Item>>(mutableListOf())
    val cartItems: MutableLiveData<MutableList<Item>> = _cartItems

    init {
        auth.currentUser?.let { user ->
            val cartRef = database.child("carts").child(user.uid)
            cartRef.get().addOnSuccessListener { snapshot ->
                val items = snapshot.children.mapNotNull { it.getValue(Item::class.java) }
                _cartItems.value = items.toMutableList()
            }
        }
    }

    fun addItemToCart(item: Item) {
        auth.currentUser?.let { user ->
            val cartRef = database.child("carts").child(user.uid)
            cartRef.child(item.name).setValue(item).addOnCompleteListener {
                if (it.isSuccessful) {
                    _cartItems.value?.add(item)
                    _cartItems.value = _cartItems.value
                }
            }
        }
    }

    fun removeItemFromCart(item: Item) {
        auth.currentUser?.let { user ->
            val cartRef = database.child("carts").child(user.uid)
            cartRef.child(item.name).removeValue().addOnCompleteListener {
                if (it.isSuccessful) {
                    _cartItems.value?.remove(item)
                    _cartItems.value = _cartItems.value
                }
            }
        }
    }

    fun clearCart() {
        auth.currentUser?.let { user ->
            val cartRef = database.child("carts").child(user.uid)
            cartRef.removeValue().addOnCompleteListener {
                if (it.isSuccessful) {
                    _cartItems.value?.clear()
                    _cartItems.value = _cartItems.value
                }
            }
        }
    }

    fun getCartItems(): List<Item> {
        return _cartItems.value ?: emptyList()
    }
}
