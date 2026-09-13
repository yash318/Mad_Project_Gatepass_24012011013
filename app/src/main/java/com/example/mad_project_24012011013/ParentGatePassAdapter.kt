package com.example.mad_project_24012011013

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore

class ParentGatePassAdapter(
    private val context: Context,
    private val gatePassList: List<GatePass>
) : RecyclerView.Adapter<ParentGatePassAdapter.GatePassViewHolder>() {

    class GatePassViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvPlace: TextView =
            itemView.findViewById(R.id.tvPlace)

        val tvReason: TextView =
            itemView.findViewById(R.id.tvReason)

        val tvExit: TextView =
            itemView.findViewById(R.id.tvExit)

        val tvReturn: TextView =
            itemView.findViewById(R.id.tvReturn)

        val tvParentApproval: TextView =
            itemView.findViewById(R.id.tvParentApproval)

        val tvRectorApproval: TextView =
            itemView.findViewById(R.id.tvRectorApproval)

        val tvStatus: TextView =
            itemView.findViewById(R.id.tvStatus)

        val btnApprove: MaterialButton =
            itemView.findViewById(R.id.btnApprove)

        val btnReject: MaterialButton =
            itemView.findViewById(R.id.btnReject)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GatePassViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_gate_pass,
                parent,
                false
            )

        return GatePassViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: GatePassViewHolder,
        position: Int
    ) {

        val gatePass = gatePassList[position]

        holder.tvPlace.text =
            "Place: ${gatePass.place}"

        holder.tvReason.text =
            "Reason: ${gatePass.reason}"

        holder.tvExit.text =
            "Exit: ${gatePass.exitDate} ${gatePass.exitTime}"

        holder.tvReturn.text =
            "Return: ${gatePass.returnDate} ${gatePass.returnTime}"

        holder.tvParentApproval.text =
            if (gatePass.parentApproval) {
                "Parent: Approved"
            } else {
                "Parent: Pending"
            }

        holder.tvRectorApproval.text =
            if (gatePass.rectorApproval) {
                "Rector: Approved"
            } else {
                "Rector: Pending"
            }

        holder.tvStatus.text =
            "Status: ${gatePass.status}"

        holder.itemView.setOnClickListener {

            val intent = Intent(
                context,
                GatePassDetailsActivity::class.java
            )

            intent.putExtra(
                "gatePassId",
                gatePass.id
            )

            context.startActivity(intent)
        }

        // Approve button
        holder.btnApprove.setOnClickListener {

            val db = FirebaseFirestore.getInstance()

            val newStatus = if (gatePass.rectorApproval) {
                "APPROVED"
            } else {
                "PARTIALLY_APPROVED"
            }

            db.collection("gatePasses")
                .document(gatePass.id)
                .update(
                    mapOf(
                        "parentApproval" to true,
                        "status" to newStatus
                    )
                )
                .addOnSuccessListener {

                    Toast.makeText(
                        context,
                        "Gate pass approved",
                        Toast.LENGTH_SHORT
                    ).show()

                    gatePass.parentApproval = true
                    gatePass.status = newStatus

                    holder.tvParentApproval.text =
                        "Parent: Approved"

                    holder.tvStatus.text =
                        "Status: $newStatus"

                    holder.btnApprove.isEnabled = false
                    holder.btnReject.isEnabled = false
                }
                .addOnFailureListener {

                    Toast.makeText(
                        context,
                        "Failed to approve gate pass",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }

        // Reject button
        holder.btnReject.setOnClickListener {

            val db = FirebaseFirestore.getInstance()

            db.collection("gatePasses")
                .document(gatePass.id)
                .update(
                    "status", "REJECTED"
                )
                .addOnSuccessListener {

                    Toast.makeText(
                        context,
                        "Gate pass rejected",
                        Toast.LENGTH_SHORT
                    ).show()

                    gatePass.status = "REJECTED"

                    holder.tvStatus.text =
                        "Status: REJECTED"

                    holder.btnApprove.isEnabled = false
                    holder.btnReject.isEnabled = false
                }
                .addOnFailureListener {

                    Toast.makeText(
                        context,
                        "Failed to reject gate pass",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    override fun getItemCount(): Int {
        return gatePassList.size
    }
}