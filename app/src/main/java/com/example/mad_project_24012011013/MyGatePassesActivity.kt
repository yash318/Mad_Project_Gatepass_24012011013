package com.example.mad_project_24012011013

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyGatePassesActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var recyclerGatePasses: RecyclerView

    private val gatePassList = ArrayList<GatePass>()
    private lateinit var adapter: GatePassAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_gate_passes)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        recyclerGatePasses =
            findViewById(R.id.recyclerGatePasses)

        val btnBack =
            findViewById<MaterialButton>(R.id.btnBack)

        adapter = GatePassAdapter(this, gatePassList)

        recyclerGatePasses.layoutManager =
            LinearLayoutManager(this)

        recyclerGatePasses.adapter = adapter

        loadGatePasses()

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadGatePasses() {

        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(
                this,
                "Please login again",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        db.collection("gatePasses")
            .whereEqualTo("studentId", currentUser.uid)
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
                            document.getBoolean("parentApproval") ?: false,

                        rectorApproval =
                            document.getBoolean("rectorApproval") ?: false,

                        status =
                            document.getString("status") ?: "PENDING"
                    )

                    gatePassList.add(gatePass)
                }

                adapter.notifyDataSetChanged()

                if (gatePassList.isEmpty()) {
                    Toast.makeText(
                        this,
                        "No gate passes found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Failed to load gate passes",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}