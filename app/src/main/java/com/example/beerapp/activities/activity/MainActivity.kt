package com.example.beerapp.activities.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.beerapp.R
import com.example.beerapp.utils.FirebaseUtils
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private var progressStatus = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Initialize progress bar
        progressBar = findViewById(R.id.progressBar)

        // Check Firebase Auth state
        val currentUser = FirebaseUtils.getCurrentUser()
        if (currentUser != null) {
            // User is signed in, redirect to Dashboard
            Log.d("MainActivity", "User is signed in: ${currentUser.uid}")
            startActivity(Intent(this@MainActivity, Dashboard::class.java))
            finish()
        } else {
            // User is not signed in, start progress
            Log.d("MainActivity", "No user signed in, starting progress")
            Handler().postDelayed({
                updateProgress()
            }, 10)
        }
    }

    private fun updateProgress() {
        val thread = Thread {
            while (progressStatus < 100) {
                try {
                    Thread.sleep(30)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                progressStatus++
                runOnUiThread {
                    progressBar.progress = progressStatus
                }
            }
            // When progress reaches 100%, start the LoginActivity
            Log.d("MainActivity", "Progress complete, starting LoginActivity")
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        thread.start()
    }
}
