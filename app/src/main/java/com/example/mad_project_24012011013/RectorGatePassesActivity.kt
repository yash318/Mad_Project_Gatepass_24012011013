package com.example.mad_project_24012011013

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore

class RectorGatePassesActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    private lateinit var recyclerGatePasses: RecyclerView

    private val gatePassList = mutableListOf<GatePass>()
    private lateinit var adapter: RectorGatePassAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_rector_gate_passes)

        db = FirebaseFirestore.getInstance()

        recyclerGatePasses =
            findViewById(R.id.recyclerRectorGatePasses)

        val btnBack =
            findViewById<MaterialButton>(R.id.btnBack)

        recyclerGatePasses.layoutManager =
            LinearLayoutManager(this)

        adapter =
            RectorGatePassAdapter(this, gatePassList)

        recyclerGatePasses.adapter = adapter

        btnBack.setOnClickListener {
            finish()
        }

        loadGatePasses()
    }

    private fun loadGatePasses() {

        db.collection("gatePasses")
            .get()
            .addOnSuccessListener { documents ->

                gatePassList.clear()

                for (document in documents) {

                    val gatePass = GatePass(
                        id = document.id,

                        studentName =
                            document.getString("studentName") ?: "",

                        place =
                            document.getString("place") ?: "",

                        reason =
                            document.getString("reason") ?: "",

                        description =
                            document.getString("description") ?: "",

                        exitDate =
                            document.getString("exitDate") ?: "",

                        exitTime =
                            document.getString("exitTime") ?: "",

                        returnDate =
                            document.getString("returnDate") ?: "",

                        returnTime =
                            document.getString("returnTime") ?: "",

                        parentApproval =
                            document.getBoolean("parentApproval")
                                ?: false,

                        rectorApproval =
                            document.getBoolean("rectorApproval")
                                ?: false,

                        status =
                            document.getString("status")
                                ?: "PENDING"
                    )

                    gatePassList.add(gatePass)
                }

                adapter.notifyDataSetChanged()

                if (gatePassList.isEmpty()) {

                    Toast.makeText(
                        this,
                        "No gate pass requests found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Unable to load gate pass requests",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}