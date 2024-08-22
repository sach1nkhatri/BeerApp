package com.example.beerapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beerapp.R
import com.example.beerapp.ViewModel.CartViewModel
import com.example.beerapp.activities.adapter.Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CartFragment : Fragment() {

    private lateinit var cartAdapter: CartAdapter
    private lateinit var viewModel: CartViewModel
    private lateinit var totalPriceTextView: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(CartViewModel::class.java)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Initialize views
        totalPriceTextView = view.findViewById(R.id.totalprice)
        val recyclerView: RecyclerView = view.findViewById(R.id.cartItemView)
        val checkoutButton: Button = view.findViewById(R.id.checkoutBtn)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        cartAdapter = CartAdapter(emptyList())
        recyclerView.adapter = cartAdapter

        // Observe cart items and update the RecyclerView
        viewModel.cartItems.observe(viewLifecycleOwner) { items ->
            cartAdapter.updateData(items)
            updateTotalPrice(items)
        }

        // Set up swipe-to-delete functionality
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT, // Swipe directions
            ItemTouchHelper.RIGHT // Only swipe to the right for deletion
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // No move operation needed
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item = cartAdapter.getItemAt(position)
                viewModel.removeItemFromCart(item)
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)

        checkoutButton.setOnClickListener {
            showCheckoutConfirmationDialog()
        }
    }

    private fun updateTotalPrice(items: List<Item>) {
        val totalPrice = items.sumOf { extractPrice(it.price) }
        totalPriceTextView.text = "Total: Rs $totalPrice/-"
    }

    private fun extractPrice(priceString: String): Double {
        // Remove any non-numeric characters (e.g., "Rs", "/-")
        return priceString.replace(Regex("[^\\d.]"), "").toDoubleOrNull() ?: 0.0
    }

    private fun showCheckoutConfirmationDialog() {
        val totalPrice = totalPriceTextView.text.toString()
        AlertDialog.Builder(requireContext())
            .setTitle("Checkout Confirmation")
            .setMessage("Do you want to continue your order?\n$totalPrice")
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                // Handle checkout action here
                handleCheckout()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun handleCheckout() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            // Fetch cart items
            viewModel.cartItems.value?.let { items ->
                // Create a unique order entry
                val orderKey = database.child("orderHistory").child(userId).push().key ?: return

                // Prepare checkout data
                val checkoutData = mapOf(
                    "items" to items.map { it.toMap() }, // Convert items to a map
                    "totalPrice" to totalPriceTextView.text.toString(),
                    "timestamp" to System.currentTimeMillis()
                )

                // Save checkout data to Firebase
                database.child("orderHistory").child(userId).child(orderKey).setValue(checkoutData)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Clear the cart
                            viewModel.clearCart()
                            // Notify user
                            AlertDialog.Builder(requireContext())
                                .setTitle("Order Successful")
                                .setMessage("Your order has been placed successfully.")
                                .setPositiveButton("OK") { dialog, _ ->
                                    dialog.dismiss()
                                    // Optionally, navigate back to another screen
                                }
                                .create()
                                .show()
                        } else {
                            // Handle the error
                            AlertDialog.Builder(requireContext())
                                .setTitle("Checkout Failed")
                                .setMessage("Failed to save checkout data: ${task.exception?.message}")
                                .setPositiveButton("OK") { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .create()
                                .show()
                        }
                    }
            }
        }
    }
}
