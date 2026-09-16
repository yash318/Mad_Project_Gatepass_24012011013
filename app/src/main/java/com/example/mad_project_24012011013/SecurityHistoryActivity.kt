package com.example.mad_project_24012011013

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore

class SecurityHistoryActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerHistory: RecyclerView

    private val recordList = mutableListOf<GateRecord>()
    private lateinit var adapter: SecurityHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_security_history)

        db = FirebaseFirestore.getInstance()

        recyclerHistory =
            findViewById(R.id.recyclerSecurityHistory)

        val btnBack =
            findViewById<MaterialButton>(R.id.btnBack)

        recyclerHistory.layoutManager =
            LinearLayoutManager(this)

        adapter = SecurityHistoryAdapter(recordList)

        recyclerHistory.adapter = adapter

        btnBack.setOnClickListener {
            finish()
        }

        loadGateRecords()
    }

    private fun loadGateRecords() {

        db.collection("gateRecords")
            .get()
            .addOnSuccessListener { documents ->

                recordList.clear()

                for (document in documents) {

                    val timestampValue =
                        document.getTimestamp("timestamp")

                    val timestampText =
                        timestampValue?.toDate()?.toString()
                            ?: "Not available"

                    val record = GateRecord(
                        id = document.id,

                        gatePassId =
                            document.getString("gatePassId")
                                ?: "",

                        studentId =
                            document.getString("studentId")
                                ?: "",

                        securityGuardId =
                            document.getString("securityGuardId")
                                ?: "",

                        type =
                            document.getString("type")
                                ?: "",

                        timestamp = timestampText
                    )

                    recordList.add(record)
                }

                adapter.notifyDataSetChanged()

                if (recordList.isEmpty()) {

                    Toast.makeText(
                        this,
                        "No gate records found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Unable to load gate records",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}