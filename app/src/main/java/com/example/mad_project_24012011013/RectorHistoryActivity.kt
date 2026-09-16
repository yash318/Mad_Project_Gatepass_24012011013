package com.example.mad_project_24012011013

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore

class RectorHistoryActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    private lateinit var recyclerHistory: RecyclerView

    private val historyList = mutableListOf<GatePass>()
    private lateinit var adapter: RectorHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_rector_history)

        db = FirebaseFirestore.getInstance()

        recyclerHistory =
            findViewById(R.id.recyclerRectorHistory)

        val btnBack =
            findViewById<MaterialButton>(R.id.btnBack)

        recyclerHistory.layoutManager =
            LinearLayoutManager(this)

        adapter =
            RectorHistoryAdapter(this, historyList)

        recyclerHistory.adapter = adapter

        btnBack.setOnClickListener {
            finish()
        }

        loadHistory()
    }

    private fun loadHistory() {

        db.collection("gatePasses")
            .get()
            .addOnSuccessListener { documents ->

                historyList.clear()

                for (document in documents) {

                    val status =
                        document.getString("status") ?: "PENDING"

                    // Only processed gate passes
                    if (status == "APPROVED" ||
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

                        historyList.add(gatePass)
                    }
                }

                adapter.notifyDataSetChanged()

                if (historyList.isEmpty()) {

                    Toast.makeText(
                        this,
                        "No gate pass history found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Unable to load gate pass history",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}