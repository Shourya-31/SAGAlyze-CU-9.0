package com.example.sagalyze.app

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sagalyze.R
import com.example.sagalyze.report.ReportActivity

class PatientAdapter(
    private val patients: MutableList<Patient>, // ✅ MutableList so updateList works
    private val onPatientClick: (Patient) -> Unit
) : RecyclerView.Adapter<PatientAdapter.PatientViewHolder>() {

    inner class PatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPatientName: TextView = itemView.findViewById(R.id.tvPatientName)
        val tvPatientInfo: TextView = itemView.findViewById(R.id.tvPatientInfo)
        val ivAvatar: ImageView = itemView.findViewById(R.id.ivAvatar)
        val ivChevron: ImageView = itemView.findViewById(R.id.ivChevron)
        val eyeIcon: ImageView = itemView.findViewById(R.id.ivEye)

        fun bind(patient: Patient) {
            tvPatientName.text = patient.name
            tvPatientInfo.text = "${patient.age} • ${patient.gender} • ${patient.condition}"

            // Whole item click (optional)
            itemView.setOnClickListener {
                onPatientClick(patient)
            }

            // 👁️ Eye icon click → open ReportActivity
//            eyeIcon.setOnClickListener {
//                val context = itemView.context
//                val intent = Intent(context, ReportActivity::class.java).apply {
//                    putExtra("PATIENT_ID", patient.id)
//                    putExtra("PATIENT_NAME", patient.name)
//                    putExtra("PATIENT_CONDITION", patient.condition)
//                }
//                context.startActivity(intent)
//            }
            eyeIcon.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, ReportActivity::class.java).apply {
                    putExtra("PATIENT_ID", patient.id)
                    putExtra("PATIENT_NAME", patient.name)
                    putExtra("PATIENT_CONDITION", patient.condition)
                    putExtra("PATIENT_AGE", patient.age)
                    putExtra("PATIENT_GENDER", patient.gender)
                    putExtra("PATIENT_BLOOD_GROUP", patient.bloodGroup)
                    putExtra("PATIENT_ID", patient.id?.toString() ?: "N/A")
                }
                context.startActivity(intent)
            }

        }
    }

    fun updateList(newList: List<Patient>) {
        patients.clear()
        patients.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_patient, parent, false)
        return PatientViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val patient = patients[position]
        holder.bind(patient)
    }

    override fun getItemCount(): Int = patients.size // ✅ Moved OUTSIDE onBindViewHolder()
}
