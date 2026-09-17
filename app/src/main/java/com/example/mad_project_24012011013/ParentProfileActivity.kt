package com.example.mad_project_24012011013

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ParentProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvRole: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvChild: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        tvName = findViewById(R.id.tvParentName)
        tvEmail = findViewById(R.id.tvParentEmail)
        tvRole = findViewById(R.id.tvParentRole)
        tvStatus = findViewById(R.id.tvParentStatus)
        tvChild = findViewById(R.id.tvParentChild)

        findViewById<MaterialButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        loadProfile()
    }

    private fun loadProfile() {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        db.collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    Toast.makeText(this, "Parent profile not found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val name = document.getString("name") ?: "Parent"
                val email = document.getString("email") ?: currentUser.email ?: "Not available"
                val role = document.getString("role") ?: "parent"
                val status = document.getString("status") ?: "active"
                val childId = document.getString("childId") ?: ""

                tvName.text = name
                tvEmail.text = email
                tvRole.text = role.replaceFirstChar { it.uppercase() }
                tvStatus.text = status.replaceFirstChar { it.uppercase() }

                if (childId.isNotEmpty()) {
                    loadChild(childId)
                } else {
                    tvChild.text = "Not linked yet"
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Unable to load profile", Toast.LENGTH_LONG).show()
            }
    }

    private fun loadChild(childId: String) {
        db.collection("users")
            .document(childId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val childName = document.getString("name") ?: "Student"
                    val childEmail = document.getString("email") ?: ""
                    tvChild.text = if (childEmail.isNotEmpty()) {
                        "$childName\n$childEmail"
                    } else {
                        childName
                    }
                } else {
                    tvChild.text = "Student profile not found"
                }
            }
            .addOnFailureListener {
                tvChild.text = "Unable to load student"
            }
    }
}
