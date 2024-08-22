package com.example.beerapp.fragments

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beerapp.R
import com.example.beerapp.activities.activity.LoginActivity
import com.example.beerapp.activities.adapter.OrderHistoryAdapter
import com.example.beerapp.activities.adapter.HistoryItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileFragment : Fragment(), SensorEventListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastShakeTime: Long = 0

    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var logoutButton: Button
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        nameTextView = view.findViewById(R.id.textViewName)
        emailTextView = view.findViewById(R.id.textViewEmail)
        phoneTextView = view.findViewById(R.id.textViewPhone)
        logoutButton = view.findViewById(R.id.EditProfile)
        historyRecyclerView = view.findViewById(R.id.historyRecyclerView)

        // Initialize adapter for ProfileFragment
        orderHistoryAdapter = OrderHistoryAdapter(emptyList(), requireContext(), useDetailLayout = false)
        historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        historyRecyclerView.adapter = orderHistoryAdapter

        loadUserData()
        loadOrderHistory()

        logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    private fun loadUserData() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            database.child("users").child(userId).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val name = snapshot.child("name").value.toString()
                    val email = snapshot.child("email").value.toString()
                    val phone = snapshot.child("phone").value.toString()

                    nameTextView.text = name
                    emailTextView.text = email
                    phoneTextView.text = phone
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load user data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadOrderHistory() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            database.child("orderHistory").child(userId).get().addOnSuccessListener { snapshot ->
                val historyItems = snapshot.children.mapNotNull { orderSnapshot ->
                    val timestamp = orderSnapshot.child("timestamp").value as? Long
                    val totalPrice = orderSnapshot.child("totalPrice").value as? String
                    val orderId = orderSnapshot.key // Assuming orderId is the key in Firebase

                    if (timestamp != null && totalPrice != null && orderId != null) {
                        HistoryItem(
                            timestamp = timestamp,
                            totalPrice = totalPrice,
                            orderId = orderId
                        )
                    } else {
                        null
                    }
                }

                Log.d("ProfileFragment", "Loaded order history: $historyItems")
                orderHistoryAdapter.updateData(historyItems)
            }.addOnFailureListener {
                Log.e("ProfileFragment", "Failed to load order history", it)
                Toast.makeText(requireContext(), "Failed to load order history", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { dialog, _ ->
                auth.signOut()
                Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val acceleration = Math.sqrt((x * x + y * y + z * z).toDouble())
            val currentTime = System.currentTimeMillis()

            if (acceleration > 12 && currentTime - lastShakeTime > 1000) {
                lastShakeTime = currentTime
                showLogoutConfirmationDialog()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
