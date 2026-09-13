package com.example.mad_project_24012011013

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ParentGatePassesActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var recyclerGatePasses: RecyclerView
    private lateinit var tvChildName: TextView

    private val gatePassList = mutableListOf<GatePass>()
    private lateinit var adapter: ParentGatePassAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent_gate_passes)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        tvChildName = findViewById(R.id.tvChildName)
        recyclerGatePasses = findViewById(R.id.recyclerParentGatePasses)

        val btnBack = findViewById<MaterialButton>(R.id.btnBack)

        recyclerGatePasses.layoutManager = LinearLayoutManager(this)

        adapter = ParentGatePassAdapter(this, gatePassList)

        recyclerGatePasses.adapter = adapter

        btnBack.setOnClickListener {
            finish()
        }

        loadChildGatePasses()
    }

    private fun loadChildGatePasses() {

        val parentId = auth.currentUser?.uid

        if (parentId == null) {
            Toast.makeText(
                this,
                "Parent not logged in",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        db.collection("users")
            .document(parentId)
            .get()
            .addOnSuccessListener { parentDocument ->

                val childId =
                    parentDocument.getString("childId")

                if (childId.isNullOrEmpty()) {

                    tvChildName.text =
                        "Child: Not linked yet"

                    Toast.makeText(
                        this,
                        "No student linked to this parent",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }

                loadChildDetails(childId)
                loadGatePasses(childId)
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Unable to load parent profile",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun loadChildDetails(childId: String) {

        db.collection("users")
            .document(childId)
            .get()
            .addOnSuccessListener { childDocument ->

                val childName =
                    childDocument.getString("name") ?: "Unknown"

                tvChildName.text =
                    "Child: $childName"
            }
    }

    private fun loadGatePasses(childId: String) {

        db.collection("gatePasses")
            .whereEqualTo("studentId", childId)
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