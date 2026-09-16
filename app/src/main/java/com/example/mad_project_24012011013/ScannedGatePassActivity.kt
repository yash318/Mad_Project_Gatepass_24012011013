package com.example.mad_project_24012011013

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue

class ScannedGatePassActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    private lateinit var tvStudentName: TextView
    private lateinit var tvPlace: TextView
    private lateinit var tvReason: TextView
    private lateinit var tvExit: TextView
    private lateinit var tvReturn: TextView
    private lateinit var tvStatus: TextView
    private lateinit var btnGateAction: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_scanned_gate_pass)

        db = FirebaseFirestore.getInstance()

        tvStudentName = findViewById(R.id.tvStudentName)
        tvPlace = findViewById(R.id.tvPlace)
        tvReason = findViewById(R.id.tvReason)
        tvExit = findViewById(R.id.tvExit)
        tvReturn = findViewById(R.id.tvReturn)
        tvStatus = findViewById(R.id.tvStatus)
        btnGateAction = findViewById(R.id.btnGateAction)

        val btnBack = findViewById<MaterialButton>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }

        val gatePassId = intent.getStringExtra("gatePassId")

        if (gatePassId.isNullOrEmpty()) {
            Toast.makeText(
                this,
                "Invalid QR code",
                Toast.LENGTH_LONG
            ).show()
            btnGateAction.isEnabled = false
            return
        }

        loadGatePass(gatePassId)
        btnGateAction.setOnClickListener {

            val currentGatePassId = gatePassId

            if (currentGatePassId.isNullOrEmpty()) {
                return@setOnClickListener
            }

            recordGateAction(currentGatePassId)
        }
    }

    private fun loadGatePass(gatePassId: String) {

        db.collection("gatePasses")
            .document(gatePassId)
            .get()
            .addOnSuccessListener { document ->

                if (!document.exists()) {

                    Toast.makeText(
                        this,
                        "Gate pass not found",
                        Toast.LENGTH_LONG
                    ).show()

                    btnGateAction.isEnabled = false
                    return@addOnSuccessListener
                }

                val studentName =
                    document.getString("studentName") ?: "Unknown"

                val place =
                    document.getString("place") ?: ""

                val reason =
                    document.getString("reason") ?: ""

                val exitDate =
                    document.getString("exitDate") ?: ""

                val exitTime =
                    document.getString("exitTime") ?: ""

                val returnDate =
                    document.getString("returnDate") ?: ""

                val returnTime =
                    document.getString("returnTime") ?: ""

                val status =
                    document.getString("status") ?: "PENDING"

                tvStudentName.text = "Student: $studentName"
                tvPlace.text = "Place: $place"
                tvReason.text = "Reason: $reason"
                tvExit.text = "Exit: $exitDate $exitTime"
                tvReturn.text = "Return: $returnDate $returnTime"
                tvStatus.text = "Status: $status"

                if (status == "APPROVED") {

                    btnGateAction.text = "Record Exit"
                    btnGateAction.isEnabled = true

                } else if (status == "ACTIVE") {

                    btnGateAction.text = "Record Entry"
                    btnGateAction.isEnabled = true

                } else {

                    btnGateAction.isEnabled = false

                    Toast.makeText(
                        this,
                        "Gate pass is not active",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Unable to load gate pass",
                    Toast.LENGTH_LONG
                ).show()

                btnGateAction.isEnabled = false
            }
    }
    private fun recordGateAction(gatePassId: String) {

        val securityGuardId = FirebaseAuth.getInstance().currentUser?.uid

        if (securityGuardId == null) {
            Toast.makeText(
                this,
                "Security login required",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        db.collection("gatePasses")
            .document(gatePassId)
            .get()
            .addOnSuccessListener { document ->

                if (!document.exists()) {
                    Toast.makeText(
                        this,
                        "Gate pass not found",
                        Toast.LENGTH_LONG
                    ).show()
                    return@addOnSuccessListener
                }

                val status = document.getString("status") ?: "PENDING"
                val studentId = document.getString("studentId") ?: ""

                if (status == "APPROVED") {

                    val record = hashMapOf(
                        "gatePassId" to gatePassId,
                        "studentId" to studentId,
                        "securityGuardId" to securityGuardId,
                        "type" to "EXIT",
                        "timestamp" to FieldValue.serverTimestamp()
                    )

                    db.collection("gateRecords")
                        .add(record)
                        .addOnSuccessListener {

                            db.collection("gatePasses")
                                .document(gatePassId)
                                .update("status", "ACTIVE")
                                .addOnSuccessListener {

                                    Toast.makeText(
                                        this,
                                        "Exit recorded successfully",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    loadGatePass(gatePassId)
                                }
                        }

                } else if (status == "ACTIVE") {

                    val record = hashMapOf(
                        "gatePassId" to gatePassId,
                        "studentId" to studentId,
                        "securityGuardId" to securityGuardId,
                        "type" to "ENTRY",
                        "timestamp" to FieldValue.serverTimestamp()
                    )

                    db.collection("gateRecords")
                        .add(record)
                        .addOnSuccessListener {

                            db.collection("gatePasses")
                                .document(gatePassId)
                                .update("status", "COMPLETED")
                                .addOnSuccessListener {

                                    Toast.makeText(
                                        this,
                                        "Entry recorded successfully",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    loadGatePass(gatePassId)
                                }
                        }

                } else {

                    Toast.makeText(
                        this,
                        "Gate pass cannot be processed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}