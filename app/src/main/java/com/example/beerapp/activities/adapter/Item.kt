package com.example.beerapp.activities.adapter


data class Item(
    val name: String = "",
    val imageResId: Int = 0,
    val price: String = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "imageResId" to imageResId,
            "price" to price
        )
    }
}
