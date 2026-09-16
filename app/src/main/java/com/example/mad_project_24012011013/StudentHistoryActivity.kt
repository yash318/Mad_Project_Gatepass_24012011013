package com.example.mad_project_24012011013

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StudentHistoryActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerHistory: RecyclerView

    private val gatePassList = mutableListOf<GatePass>()
    private lateinit var adapter: ParentHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_student_history)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        recyclerHistory =
            findViewById(R.id.recyclerStudentHistory)

        val btnBack =
            findViewById<MaterialButton>(R.id.btnBack)

        recyclerHistory.layoutManager =
            LinearLayoutManager(this)

        adapter = ParentHistoryAdapter(
            this,
            gatePassList
        )

        recyclerHistory.adapter = adapter

        btnBack.setOnClickListener {
            finish()
        }

        loadHistory()
    }

    private fun loadHistory() {

        val studentId = auth.currentUser?.uid

        if (studentId == null) {
            Toast.makeText(
                this,
                "User not logged in",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        db.collection("gatePasses")
            .whereEqualTo("studentId", studentId)
            .get()
            .addOnSuccessListener { documents ->

                gatePassList.clear()

                for (document in documents) {

                    val status =
                        document.getString("status") ?: "PENDING"

                    if (
                        status == "APPROVED" ||
                        status == "REJECTED" ||
                        status == "COMPLETED"
                    ) {

                        val gatePass = GatePass(
                            id = document.id,

                            studentName =
                                document.getString("studentName")
                                    ?: "",

                            place =
                                document.getString("place")
                                    ?: "",

                            reason =
                                document.getString("reason")
                                    ?: "",

                            description =
                                document.getString("description")
                                    ?: "",

                            exitDate =
                                document.getString("exitDate")
                                    ?: "",

                            exitTime =
                                document.getString("exitTime")
                                    ?: "",

                            returnDate =
                                document.getString("returnDate")
                                    ?: "",

                            returnTime =
                                document.getString("returnTime")
                                    ?: "",

                            parentApproval =
                                document.getBoolean("parentApproval")
                                    ?: false,

                            rectorApproval =
                                document.getBoolean("rectorApproval")
                                    ?: false,

                            status = status
                        )

                        gatePassList.add(gatePass)
                    }
                }

                adapter.notifyDataSetChanged()

                if (gatePassList.isEmpty()) {

                    Toast.makeText(
                        this,
                        "No history found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Unable to load history",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}