package com.example.mad_project_24012011013

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore

class AdminGatePassesActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var container: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_gate_passes)
        db = FirebaseFirestore.getInstance()
        container = findViewById(R.id.gatePassContainer)
        findViewById<MaterialButton>(R.id.btnBack).setOnClickListener { finish() }
        loadGatePasses()
    }

    private fun loadGatePasses() {
        db.collection("gatePasses").get().addOnSuccessListener { documents ->
            container.removeAllViews()
            if (documents.isEmpty) {
                addText("No gate passes found")
                return@addOnSuccessListener
            }

            documents.forEach { doc ->
                val card = MaterialCardView(this)
                card.radius = 20f
                card.cardElevation = 4f
                card.setContentPadding(24, 20, 24, 20)

                val box = LinearLayout(this)
                box.orientation = LinearLayout.VERTICAL

                val title = TextView(this)
                title.text = doc.getString("studentName") ?: "Student"
                title.textSize = 19f
                title.setTypeface(null, android.graphics.Typeface.BOLD)

                val details = TextView(this)
                details.text = "Place: ${doc.getString("place") ?: ""}\n" +
                        "Reason: ${doc.getString("reason") ?: ""}\n" +
                        "Exit: ${doc.getString("exitDate") ?: ""} ${doc.getString("exitTime") ?: ""}\n" +
                        "Return: ${doc.getString("returnDate") ?: ""} ${doc.getString("returnTime") ?: ""}\n" +
                        "Parent: ${if (doc.getBoolean("parentApproval") == true) "Approved" else "Pending"}\n" +
                        "Rector: ${if (doc.getBoolean("rectorApproval") == true) "Approved" else "Pending"}\n" +
                        "Status: ${doc.getString("status") ?: "PENDING"}"
                details.textSize = 15f
                details.setPadding(0, 8, 0, 0)

                box.addView(title)
                box.addView(details)
                card.addView(box)

                val params = LinearLayout.LayoutParams(-1, -2)
                params.setMargins(0, 0, 0, 16)
                container.addView(card, params)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Unable to load gate passes", Toast.LENGTH_LONG).show()
        }
    }

    private fun addText(value: String) {
        val tv = TextView(this)
        tv.text = value
        tv.textSize = 17f
        tv.setPadding(8, 20, 8, 20)
        container.addView(tv)
    }
}
