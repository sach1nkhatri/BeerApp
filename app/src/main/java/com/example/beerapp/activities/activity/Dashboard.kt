package com.example.beerapp.activities.activity

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.beerapp.R
import com.example.beerapp.fragments.CartFragment
import com.example.beerapp.fragments.HomeFragment
import com.example.beerapp.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class Dashboard : AppCompatActivity() {

    private lateinit var textViewDashboard: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        // Initialize the TextView
        textViewDashboard = findViewById(R.id.textViewDashboard)

        // Set up the bottom navigation view
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.buttonNavigationView)

        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            textViewDashboard.text = getString(R.string.grab_a_beer) // Set initial text
        }

        // Handle bottom navigation item selections
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeNav -> {
                    loadFragment(HomeFragment())
                    textViewDashboard.text = getString(R.string.grab_a_beer)
                    true
                }
                R.id.TaskNav -> {
                    loadFragment(ProfileFragment())
                    textViewDashboard.text = "Profile" // Update to Profile text
                    true
                }
                R.id.settingsNav -> {
                    loadFragment(CartFragment())
                    textViewDashboard.text = "Cart" // Update to Cart text
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}
