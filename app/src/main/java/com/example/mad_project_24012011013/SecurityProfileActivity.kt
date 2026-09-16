package com.example.mad_project_24012011013

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SecurityProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_security_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val tvProfileName =
            findViewById<TextView>(R.id.tvProfileName)

        val tvProfileEmail =
            findViewById<TextView>(R.id.tvProfileEmail)

        val tvProfileRole =
            findViewById<TextView>(R.id.tvProfileRole)

        val tvProfileStatus =
            findViewById<TextView>(R.id.tvProfileStatus)

        val btnBack =
            findViewById<MaterialButton>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }

        val userId = auth.currentUser?.uid

        if (userId == null) {
            Toast.makeText(
                this,
                "User not logged in",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val name =
                        document.getString("name") ?: ""

                    val email =
                        document.getString("email") ?: ""

                    val role =
                        document.getString("role") ?: "security"

                    val status =
                        document.getString("status") ?: "active"

                    tvProfileName.text = "Name: $name"
                    tvProfileEmail.text = "Email: $email"
                    tvProfileRole.text = "Role: $role"
                    tvProfileStatus.text = "Status: $status"

                } else {

                    Toast.makeText(
                        this,
                        "Profile not found",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Unable to load profile",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}