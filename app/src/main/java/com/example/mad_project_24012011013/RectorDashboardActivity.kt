package com.example.mad_project_24012011013

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RectorDashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rector_dashboard)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val tvRectorWelcome =
            findViewById<TextView>(R.id.tvRectorWelcome)

        val btnRectorGatePasses =
            findViewById<MaterialButton>(R.id.btnRectorGatePasses)

        val btnRectorHistory =
            findViewById<MaterialButton>(R.id.btnRectorHistory)

        val btnRectorProfile =
            findViewById<MaterialButton>(R.id.btnRectorProfile)

        val btnRectorLogout =
            findViewById<MaterialButton>(R.id.btnRectorLogout)

        loadRectorDetails(tvRectorWelcome)

        btnRectorGatePasses.setOnClickListener {
            val intent = Intent(
                this,
                RectorGatePassesActivity::class.java
            )
            startActivity(intent)
        }

        btnRectorHistory.setOnClickListener {
            val intent = Intent(
                this,
                RectorHistoryActivity::class.java
            )
            startActivity(intent)
        }

        btnRectorProfile.setOnClickListener {
            val intent = Intent(
                this,
                RectorProfileActivity::class.java
            )
            startActivity(intent)
        }

        btnRectorLogout.setOnClickListener {

            auth.signOut()

            Toast.makeText(
                this,
                "Logged out successfully",
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent(
                this,
                MainActivity::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
        }
    }

    private fun loadRectorDetails(
        tvRectorWelcome: TextView
    ) {

        val userId = auth.currentUser?.uid

        if (userId == null) {
            return
        }

        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->

                val name =
                    document.getString("name") ?: "Rector"

                tvRectorWelcome.text =
                    "Welcome, $name 👋"
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Unable to load rector profile",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}