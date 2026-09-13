package com.example.mad_project_24012011013

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.content.Intent
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class GatePass(
    val id: String,
    val studentName: String,
    val place: String,
    val reason: String,
    val description: String,
    val exitDate: String,
    val exitTime: String,
    val returnDate: String,
    val returnTime: String,
    var parentApproval: Boolean,
    var rectorApproval: Boolean,
    var status: String
)

class GatePassAdapter(
    private val context: Context,
    private val gatePassList: List<GatePass>
) : RecyclerView.Adapter<GatePassAdapter.GatePassViewHolder>() {

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
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GatePassViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gate_pass, parent, false)

        return GatePassViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: GatePassViewHolder,
        position: Int
    ) {

        val gatePass = gatePassList[position]

        holder.tvPlace.text = gatePass.place
        holder.tvReason.text = "Reason: ${gatePass.reason}"

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
    }

    override fun getItemCount(): Int {
        return gatePassList.size
    }
}