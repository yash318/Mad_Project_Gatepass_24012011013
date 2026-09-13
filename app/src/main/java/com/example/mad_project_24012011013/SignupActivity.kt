package com.example.mad_project_24012011013

import android.content.Intent
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val etName = findViewById<TextInputEditText>(R.id.etName)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val etConfirmPassword =
            findViewById<TextInputEditText>(R.id.etConfirmPassword)

        val rgRole = findViewById<RadioGroup>(R.id.rgRole)

        val btnSignup = findViewById<MaterialButton>(R.id.btnSignup)
        val tvLogin = findViewById<TextView>(R.id.tvLogin)

        btnSignup.setOnClickListener {

            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            val selectedRole = when (rgRole.checkedRadioButtonId) {
                R.id.rbParent -> "parent"
                else -> "student"
            }

            if (name.isEmpty() || email.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(
                    this,
                    "Password must be at least 6 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(
                    this,
                    "Passwords do not match",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            btnSignup.isEnabled = false

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) {

                        val userId = auth.currentUser?.uid

                        if (userId != null) {

                            val user = hashMapOf(
                                "uid" to userId,
                                "name" to name,
                                "email" to email,
                                "role" to selectedRole,
                                "status" to "active"
                            )

                            db.collection("users")
                                .document(userId)
                                .set(user)
                                .addOnSuccessListener {

                                    Toast.makeText(
                                        this,
                                        "Account created successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    if (selectedRole == "parent") {

                                        val intent = Intent(
                                            this,
                                            ParentDashboardActivity::class.java
                                        )

                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or
                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK

                                        startActivity(intent)
                                        finish()

                                    } else {

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
                                }
                                .addOnFailureListener {

                                    btnSignup.isEnabled = true

                                    Toast.makeText(
                                        this,
                                        "Account created, but profile could not be saved",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        }

                    } else {

                        btnSignup.isEnabled = true

                        Toast.makeText(
                            this,
                            task.exception?.message ?: "Registration failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        tvLogin.setOnClickListener {
            val intent = Intent(
                this,
                MainActivity::class.java
            )

            startActivity(intent)
            finish()
        }
    }
}