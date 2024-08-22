package com.example.beerapp.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beerapp.R
import com.example.beerapp.activities.adapter.HistoryItem
import com.example.beerapp.activities.adapter.OrderHistoryAdapter

class HistoryDetailActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_history_detail)

        recyclerView = findViewById(R.id.HistoryItemView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize adapter for HistoryDetailActivity
        orderHistoryAdapter = OrderHistoryAdapter(emptyList(), this, useDetailLayout = true)
        recyclerView.adapter = orderHistoryAdapter

        val orderId = intent.getStringExtra("ORDER_ID")

// Add a check to see if orderId is null or empty
        if (orderId.isNullOrEmpty()) {
            Toast.makeText(this, "Order ID is missing", Toast.LENGTH_SHORT).show()
        } else {
            loadOrderDetails(orderId)
        }

    }

    private fun loadOrderDetails(orderId: String?) {
        // Implement the logic to load order details based on the orderId
        // and update the adapter with the loaded data
    }
}
