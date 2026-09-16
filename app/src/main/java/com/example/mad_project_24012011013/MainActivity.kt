package com.example.mad_project_24012011013

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        if (auth.currentUser != null) {
            checkUserRole()
            return
        }

        setContentView(R.layout.activity_main)

        val etEmail =
            findViewById<TextInputEditText>(R.id.etEmail)

        val etPassword =
            findViewById<TextInputEditText>(R.id.etPassword)

        val btnLogin =
            findViewById<MaterialButton>(R.id.btnLogin)

        val tvCreateAccount =
            findViewById<TextView>(R.id.tvCreateAccount)

        btnLogin.setOnClickListener {

            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please enter email and password",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            btnLogin.isEnabled = false

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->

                    btnLogin.isEnabled = true

                    if (task.isSuccessful) {

                        Toast.makeText(
                            this,
                            "Login successful",
                            Toast.LENGTH_SHORT
                        ).show()

                        checkUserRole()

                    } else {

                        Toast.makeText(
                            this,
                            task.exception?.message ?: "Login failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        tvCreateAccount.setOnClickListener {

            val intent = Intent(
                this,
                SignupActivity::class.java
            )

            startActivity(intent)
        }
    }

    private fun checkUserRole() {

        val userId = auth.currentUser?.uid

        if (userId == null) {
            return
        }

        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val role = document.getString("role")
                    Toast.makeText(
                        this,
                        "UID = [$userId]\nRole = [$role]",
                        Toast.LENGTH_LONG
                    ).show()
                    when (role) {

                        "student" -> {
                            openStudentDashboard()
                        }

                        "parent" -> {
                            openParentDashboard()
                        }
                        "rector" -> {
                            openRectorDashboard()
                        }
                        "security" -> {
                            openSecurityDashboard()
                        }
                        "admin" -> {
                            openAdminDashboard()
                        }
                        else -> {
                            Toast.makeText(this, "Invalid user role", Toast.LENGTH_LONG).show()
                            auth.signOut()
                        }
                    }

                } else {

                    Toast.makeText(
                        this,
                        "User profile not found",
                        Toast.LENGTH_LONG
                    ).show()

                    auth.signOut()
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Unable to load user profile",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun openStudentDashboard() {

        val intent = Intent(
            this,
            StudentDashboardActivity::class.java
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }

    private fun openParentDashboard() {

        val intent = Intent(
            this,
            ParentDashboardActivity::class.java
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }
    private fun openRectorDashboard() {
        val intent = Intent(
            this,
            RectorDashboardActivity::class.java
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }
    private fun openSecurityDashboard() {
        val intent = Intent(this, SecurityDashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    private fun openAdminDashboard() {

        val intent =
            Intent(this, AdminDashboardActivity::class.java)

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }
}