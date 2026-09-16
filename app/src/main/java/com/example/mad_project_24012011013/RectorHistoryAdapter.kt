package com.example.mad_project_24012011013

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RectorHistoryAdapter(
    private val context: Context,
    private val historyList: List<GatePass>
) : RecyclerView.Adapter<RectorHistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) :
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
    ): HistoryViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_gate_pass,
                parent,
                false
            )

        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: HistoryViewHolder,
        position: Int
    ) {

        val gatePass = historyList[position]

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

        // History items can still be opened
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
        return historyList.size
    }
}