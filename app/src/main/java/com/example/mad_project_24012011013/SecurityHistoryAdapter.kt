package com.example.mad_project_24012011013

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SecurityHistoryAdapter(
    private val recordList: List<GateRecord>
) : RecyclerView.Adapter<SecurityHistoryAdapter.RecordViewHolder>() {

    class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvRecordType: TextView =
            itemView.findViewById(R.id.tvRecordType)

        val tvGatePassId: TextView =
            itemView.findViewById(R.id.tvGatePassId)

        val tvStudentId: TextView =
            itemView.findViewById(R.id.tvStudentId)

        val tvRecordTime: TextView =
            itemView.findViewById(R.id.tvRecordTime)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecordViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gate_record, parent, false)

        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: RecordViewHolder,
        position: Int
    ) {

        val record = recordList[position]

        holder.tvRecordType.text = "Type: ${record.type}"

        holder.tvGatePassId.text =
            "Gate Pass: ${record.gatePassId}"

        holder.tvStudentId.text =
            "Student ID: ${record.studentId}"

        holder.tvRecordTime.text =
            "Time: ${record.timestamp}"
    }

    override fun getItemCount(): Int {
        return recordList.size
    }
}