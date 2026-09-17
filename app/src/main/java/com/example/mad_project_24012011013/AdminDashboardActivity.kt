package com.example.mad_project_24012011013

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val tvAdminWelcome = findViewById<TextView>(R.id.tvAdminWelcome)
        val tvTotalUsers = findViewById<TextView>(R.id.tvTotalUsers)
        val tvTotalGatePasses = findViewById<TextView>(R.id.tvTotalGatePasses)
        val btnAdminUsers = findViewById<MaterialButton>(R.id.btnAdminUsers)
        val btnAdminGatePasses = findViewById<MaterialButton>(R.id.btnAdminGatePasses)
        val btnAdminLogout = findViewById<MaterialButton>(R.id.btnAdminLogout)

        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get().addOnSuccessListener { document ->
                tvAdminWelcome.text = "Welcome, ${document.getString("name") ?: "Admin"} 👋"
            }
            db.collection("users").get().addOnSuccessListener { documents ->
                tvTotalUsers.text = "Total Users: ${documents.size()}"
            }
            db.collection("gatePasses").get().addOnSuccessListener { documents ->
                tvTotalGatePasses.text = "Total Gate Passes: ${documents.size()}"
            }
        }

        btnAdminUsers.setOnClickListener {
            startActivity(Intent(this, AdminUsersActivity::class.java))
        }

        btnAdminGatePasses.setOnClickListener {
            startActivity(Intent(this, AdminGatePassesActivity::class.java))
        }

        btnAdminLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
