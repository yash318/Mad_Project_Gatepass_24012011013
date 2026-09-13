package com.example.mad_project_24012011013

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ParentDashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent_dashboard)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val tvParentWelcome =
            findViewById<TextView>(R.id.tvParentWelcome)

        val tvChildName =
            findViewById<TextView>(R.id.tvChildName)

        val btnParentGatePasses =
            findViewById<MaterialButton>(R.id.btnParentGatePasses)

        val btnParentHistory =
            findViewById<MaterialButton>(R.id.btnParentHistory)

        val btnParentProfile =
            findViewById<MaterialButton>(R.id.btnParentProfile)

        val btnParentLogout =
            findViewById<MaterialButton>(R.id.btnParentLogout)

        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(
                this,
                "Please login again",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        val parentId = currentUser.uid

        // Load parent profile
        db.collection("users")
            .document(parentId)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val parentName =
                        document.getString("name") ?: "Parent"

                    val childId =
                        document.getString("childId") ?: ""

                    tvParentWelcome.text =
                        "Welcome, $parentName 👋"

                    // Load child information
                    if (childId.isNotEmpty()) {

                        loadChildInformation(
                            childId,
                            tvChildName
                        )

                    } else {

                        tvChildName.text =
                            "Child: Not linked yet"
                    }

                } else {

                    Toast.makeText(
                        this,
                        "Parent profile not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Unable to load parent profile",
                    Toast.LENGTH_SHORT
                ).show()
            }

        btnParentGatePasses.setOnClickListener {

            val intent = Intent(
                this,
                ParentGatePassesActivity::class.java
            )

            startActivity(intent)
        }

        btnParentHistory.setOnClickListener {

            Toast.makeText(
                this,
                "Gate Pass History will be added later",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnParentProfile.setOnClickListener {

            Toast.makeText(
                this,
                "Parent Profile will be added later",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnParentLogout.setOnClickListener {

            auth.signOut()

            Toast.makeText(
                this,
                "Logged out successfully",
                Toast.LENGTH_SHORT
            ).show()

            val intent =
                Intent(this, MainActivity::class.java)

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
        }
    }

    private fun loadChildInformation(
        childId: String,
        tvChildName: TextView
    ) {

        db.collection("users")
            .document(childId)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val childName =
                        document.getString("name") ?: ""

                    tvChildName.text =
                        "Child: $childName"

                } else {

                    tvChildName.text =
                        "Child: Profile not found"
                }
            }
            .addOnFailureListener {

                tvChildName.text =
                    "Child: Unable to load"
            }
    }
}