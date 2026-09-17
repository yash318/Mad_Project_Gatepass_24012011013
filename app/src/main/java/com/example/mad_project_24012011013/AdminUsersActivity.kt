package com.example.mad_project_24012011013

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore

class AdminUsersActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var container: LinearLayout

    private data class Student(val id: String, val name: String, val email: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_users)

        db = FirebaseFirestore.getInstance()
        container = findViewById(R.id.usersContainer)

        findViewById<MaterialButton>(R.id.btnBack).setOnClickListener { finish() }
        loadUsers()
    }

    private fun loadUsers() {
        db.collection("users").get().addOnSuccessListener { documents ->
            container.removeAllViews()

            if (documents.isEmpty) {
                addText("No users found")
                return@addOnSuccessListener
            }

            val students = documents.filter { it.getString("role") == "student" }
                .map { Student(it.id, it.getString("name") ?: "Student", it.getString("email") ?: "") }

            documents.forEach { doc ->
                val role = doc.getString("role") ?: "unknown"
                val name = doc.getString("name") ?: "Unknown"
                val email = doc.getString("email") ?: ""

                val card = MaterialCardView(this)
                card.radius = 20f
                card.cardElevation = 4f
                card.setContentPadding(24, 20, 24, 20)
                val box = LinearLayout(this)
                box.orientation = LinearLayout.VERTICAL

                val title = TextView(this)
                title.text = "$name  •  ${role.replaceFirstChar { it.uppercase() }}"
                title.textSize = 18f
                title.setTypeface(null, android.graphics.Typeface.BOLD)

                val info = TextView(this)
                val childId = doc.getString("childId")
                info.text = if (role == "parent") {
                    "$email\nChild ID: ${childId ?: "Not linked"}"
                } else {
                    email
                }
                info.textSize = 14f
                info.setPadding(0, 8, 0, 8)

                box.addView(title)
                box.addView(info)

                if (role == "parent") {
                    val linkButton = MaterialButton(this)
                    linkButton.text = "Link Student"
                    linkButton.setOnClickListener {
                        showStudentPicker(doc.id, students)
                    }
                    box.addView(linkButton)
                }

                card.addView(box)
                val params = LinearLayout.LayoutParams(-1, -2)
                params.setMargins(0, 0, 0, 16)
                container.addView(card, params)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Unable to load users", Toast.LENGTH_LONG).show()
        }
    }

    private fun showStudentPicker(parentId: String, students: List<Student>) {
        if (students.isEmpty()) {
            Toast.makeText(this, "No students available", Toast.LENGTH_SHORT).show()
            return
        }

        val names = students.map { "${it.name} (${it.email})" }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Select Student")
            .setItems(names) { _, which ->
                val student = students[which]
                db.collection("users").document(parentId)
                    .update("childId", student.id)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Parent linked to ${student.name}", Toast.LENGTH_SHORT).show()
                        loadUsers()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Unable to link student", Toast.LENGTH_LONG).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addText(value: String) {
        val tv = TextView(this)
        tv.text = value
        tv.textSize = 17f
        tv.setPadding(8, 20, 8, 20)
        container.addView(tv)
    }
}
