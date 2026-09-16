package com.example.mad_project_24012011013

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SecurityDashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security_dashboard)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val btnScanQr = findViewById<MaterialButton>(R.id.btnScanQr)
        val btnSecurityHistory = findViewById<MaterialButton>(R.id.btnSecurityHistory)
        val btnSecurityProfile = findViewById<MaterialButton>(R.id.btnSecurityProfile)
        val btnSecurityLogout = findViewById<MaterialButton>(R.id.btnSecurityLogout)

        btnScanQr.setOnClickListener {
            val intent = Intent(this, QrScannerActivity::class.java)
            startActivity(intent)
        }

        btnSecurityHistory.setOnClickListener {
            startActivity(Intent(this, SecurityHistoryActivity::class.java))
        }

        btnSecurityProfile.setOnClickListener {
            startActivity(Intent(this, SecurityProfileActivity::class.java))
        }

        btnSecurityLogout.setOnClickListener {
            auth.signOut()

            val intent = Intent(this, MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
        }

        loadSecurityName()
    }

    private fun loadSecurityName() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val name = document.getString("name") ?: "Security Guard"

                findViewById<android.widget.TextView>(
                    R.id.tvSecurityWelcome
                ).text = "Welcome, $name 👋"
            }
    }
}