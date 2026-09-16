package com.example.mad_project_24012011013

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StudentDashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dashboard)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)

        val btnApplyGatePass =
            findViewById<MaterialButton>(R.id.btnApplyGatePass)

        val btnMyPasses =
            findViewById<MaterialButton>(R.id.btnMyPasses)
        btnMyPasses.setOnClickListener {
            val intent = Intent(this, MyGatePassesActivity::class.java)
            startActivity(intent)
        }

        val btnHistory =
            findViewById<MaterialButton>(R.id.btnHistory)

        val btnProfile =
            findViewById<MaterialButton>(R.id.btnProfile)

        val btnLogout =
            findViewById<MaterialButton>(R.id.btnLogout)

        // Get currently logged-in user
        val currentUser = auth.currentUser

        if (currentUser != null) {

            val userId = currentUser.uid

            db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->

                    if (document.exists()) {

                        val name = document.getString("name")

                        if (!name.isNullOrEmpty()) {
                            tvWelcome.text = "Welcome, $name 👋"
                        }

                    } else {

                        Toast.makeText(
                            this,
                            "User profile not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener {

                    Toast.makeText(
                        this,
                        "Unable to load profile",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        btnApplyGatePass.setOnClickListener {

            val intent = Intent(
                this,
                ApplyGatePassActivity::class.java
            )

            startActivity(intent)
        }



        btnHistory.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    StudentHistoryActivity::class.java
                )
            )
        }

        btnProfile.setOnClickListener {

            val intent = Intent(
                this,
                StudentProfileActivity::class.java
            )

            startActivity(intent)
        }

        btnLogout.setOnClickListener {

            auth.signOut()

            Toast.makeText(
                this,
                "Logged out successfully",
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent(this, MainActivity::class.java)

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
        }
    }
}