package com.example.mad_project_24012011013

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StudentProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_profile)

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

        val currentUser = auth.currentUser

        if (currentUser != null) {

            val userId = currentUser.uid

            db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->

                    if (document.exists()) {

                        val name =
                            document.getString("name") ?: "Student"

                        val email =
                            document.getString("email")
                                ?: currentUser.email
                                ?: ""

                        val role =
                            document.getString("role") ?: "student"

                        val status =
                            document.getString("status") ?: "active"

                        tvProfileName.text = name
                        tvProfileEmail.text = email
                        tvProfileRole.text =
                            "Role: ${role.replaceFirstChar { it.uppercase() }}"
                        tvProfileStatus.text =
                            "Status: ${status.replaceFirstChar { it.uppercase() }}"

                    } else {

                        Toast.makeText(
                            this,
                            "Profile not found",
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

        btnBack.setOnClickListener {
            finish()
        }
    }
}