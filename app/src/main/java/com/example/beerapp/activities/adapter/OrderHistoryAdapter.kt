package com.example.beerapp.activities.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.beerapp.R
import com.example.beerapp.activities.HistoryDetailActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class HistoryItem(
    val timestamp: Long = 0,
    val totalPrice: String = "",
    val orderId: String = ""
)

class OrderHistoryAdapter(
    private var orders: List<HistoryItem>,
    private val context: Context,
    private val useDetailLayout: Boolean = false // Parameter to determine which layout to use
) : RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val orderIdTextView: TextView = itemView.findViewById(R.id.textView2)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.historyDescription)
        private val timeTextView: TextView = itemView.findViewById(R.id.historyTime)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val order = orders[position]
                    val intent = Intent(context, HistoryDetailActivity::class.java).apply {
                        putExtra("ORDER_ID", order.orderId)
                    }
                    context.startActivity(intent)
                }
            }
        }

        fun bind(order: HistoryItem) {
            orderIdTextView.text = "Order ID: ${order.orderId}"
            descriptionTextView.text = "Amount: ${order.totalPrice}"
            timeTextView.text = "Time: ${formatTimestamp(order.timestamp)}"
        }

        private fun formatTimestamp(timestamp: Long): String {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val layoutId = if (useDetailLayout) {
            R.layout.cart_item // Use the cart_item layout for HistoryDetailActivity
        } else {
            R.layout.item_order_history // Use the item_order_history layout for ProfileFragment
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    fun updateData(newOrders: List<HistoryItem>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}

