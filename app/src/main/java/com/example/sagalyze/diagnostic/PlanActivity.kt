package com.example.sagalyze.diagnostic

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sagalyze.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.example.sagalyze.diagnostic.PlanActivity


class PlanActivity : AppCompatActivity() {

    // View references
    private lateinit var headerAccent: View
    private lateinit var summaryText: TextView
    private lateinit var editSummaryButton: ImageButton
    private lateinit var regenerateSummaryButton: ImageButton
    private lateinit var severityLowCard: MaterialCardView
    private lateinit var severityMediumCard: MaterialCardView
    private lateinit var severityHighCard: MaterialCardView
    private lateinit var severityMessageCard: MaterialCardView
    private lateinit var severityMessageText: TextView
    private lateinit var aiSuggestButton: MaterialButton
    private lateinit var doctorNotesEditText: TextInputEditText
    private lateinit var addTreatmentButton: MaterialButton
    private lateinit var treatmentRecyclerView: RecyclerView
    private lateinit var savePlanButton: MaterialButton
    private lateinit var shareButton: MaterialButton
    private lateinit var declareHealedButton: MaterialButton

    // Logic variables
    private var selectedSeverity: SeverityLevel? = null
    private var summary: String =
        "Patient presents with mild inflammatory lesions on the facial region, consistent with acne vulgaris. No signs of cystic formation observed. Skin shows slight erythema with minimal comedones. Overall condition appears manageable with topical treatment."
    private var doctorNotes: String = ""
    private val treatmentItems = mutableListOf<TreatmentItem>()
    private lateinit var treatmentAdapter: TreatmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // Inflate main layout (make sure activity_plan.xml exists in res/layout/)
            setContentView(R.layout.activity_plan)

            // Initialize all logic in isolated, try-catch-safe blocks
            initViews()
            initializeData()
            setupRecyclerView()
            setupListeners()
            updateHeaderAccent()

            // Optional: confirmation log
            Log.d("PlanActivity", "onCreate completed successfully")

        } catch (e: Exception) {
            // Log the exact cause of crash
            Log.e("PlanActivity", "Error during onCreate", e)

            // Show user-friendly feedback for debugging
            Toast.makeText(
                this,
                "⚠️ PlanActivity failed: ${e::class.simpleName} → ${e.message}",
                Toast.LENGTH_LONG
            ).show()

            // Optionally close it to avoid a blank screen
            finish()
        }
    }


    private fun showEditDialog(item: TreatmentItem) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_treatment, null)
        val titleEditText = dialogView.findViewById<TextInputEditText>(R.id.titleEditText)
        val descriptionEditText = dialogView.findViewById<TextInputEditText>(R.id.descriptionEditText)
        val typeSpinner = dialogView.findViewById<Spinner>(R.id.typeSpinner)

        // Pre-fill if editing
        titleEditText.setText(item.title)
        descriptionEditText.setText(item.description)
        typeSpinner.setSelection(item.type.ordinal)

        MaterialAlertDialogBuilder(this)
            .setTitle("Edit Treatment Item")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val newTitle = titleEditText.text.toString()
                val newDescription = descriptionEditText.text.toString()
                val newType = TreatmentType.values()[typeSpinner.selectedItemPosition]

                val index = treatmentItems.indexOf(item)
                if (index != -1) {
                    treatmentItems[index] = item.copy(
                        title = newTitle,
                        description = newDescription,
                        type = newType
                    )
                    treatmentAdapter.notifyDataSetChanged()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun initViews() {
        // Map XML IDs
        headerAccent = findViewById(R.id.headerAccent)
        summaryText = findViewById(R.id.summaryText)
        editSummaryButton = findViewById(R.id.editSummaryButton)
        regenerateSummaryButton = findViewById(R.id.regenerateSummaryButton)
        severityLowCard = findViewById(R.id.severityLowCard)
        severityMediumCard = findViewById(R.id.severityMediumCard)
        severityHighCard = findViewById(R.id.severityHighCard)
        severityMessageCard = findViewById(R.id.severityMessageCard)
        severityMessageText = findViewById(R.id.severityMessageText)
        aiSuggestButton = findViewById(R.id.aiSuggestButton)
        doctorNotesEditText = findViewById(R.id.doctorNotesEditText)
        addTreatmentButton = findViewById(R.id.addTreatmentButton)
        treatmentRecyclerView = findViewById(R.id.treatmentRecyclerView)
        savePlanButton = findViewById(R.id.savePlanButton)
        shareButton = findViewById(R.id.shareButton)
        declareHealedButton = findViewById(R.id.declareHealedButton)
    }

    private fun initializeData() {
        treatmentItems.addAll(
            listOf(
                TreatmentItem("1", TreatmentType.MEDICATION, "Benzoyl Peroxide 5%", "Apply twice daily after cleansing"),
                TreatmentItem("2", TreatmentType.LIFESTYLE, "Gentle Cleansing Routine", "Use mild, non-comedogenic cleanser morning and evening"),
                TreatmentItem("3", TreatmentType.FOLLOW_UP, "Follow-up Appointment", "Schedule review in 4 weeks")
            )
        )
    }

    private fun setupRecyclerView() {
        summaryText.text = summary
        treatmentAdapter = TreatmentAdapter(
            treatmentItems,
            onEdit = { showEditDialog(it) },
            onDelete = { deleteTreatmentItem(it) }
        )
        treatmentRecyclerView.layoutManager = LinearLayoutManager(this)
        treatmentRecyclerView.adapter = treatmentAdapter
    }

    private fun setupListeners() {
        editSummaryButton.setOnClickListener { showEditSummaryDialog() }
        regenerateSummaryButton.setOnClickListener { regenerateSummary() }

        severityLowCard.setOnClickListener { selectSeverity(SeverityLevel.LOW) }
        severityMediumCard.setOnClickListener { selectSeverity(SeverityLevel.MEDIUM) }
        severityHighCard.setOnClickListener { selectSeverity(SeverityLevel.HIGH) }

        aiSuggestButton.setOnClickListener { generateAISuggestion() }

        doctorNotesEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                doctorNotes = s.toString()
            }
        })

        addTreatmentButton.setOnClickListener { showAddTreatmentDialog() }
        savePlanButton.setOnClickListener { savePlan() }
        shareButton.setOnClickListener { shareWithPatient() }
        declareHealedButton.setOnClickListener { declarePatientHealed() }
    }

    private fun selectSeverity(severity: SeverityLevel) {
        selectedSeverity = severity
        updateSeveritySelection()
        updateHeaderAccent()
    }

    private fun updateSeveritySelection() {
        severityLowCard.isSelected = false
        severityMediumCard.isSelected = false
        severityHighCard.isSelected = false

        when (selectedSeverity) {
            SeverityLevel.LOW -> {
                severityLowCard.isSelected = true
                severityMessageCard.visibility = View.VISIBLE
                severityMessageText.text = "Condition is mild. Routine care recommended."
                severityMessageCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.severity_low_bg))
            }

            SeverityLevel.MEDIUM -> {
                severityMediumCard.isSelected = true
                severityMessageCard.visibility = View.VISIBLE
                severityMessageText.text = "Moderate condition. Regular monitoring advised."
                severityMessageCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.severity_medium_bg))
            }

            SeverityLevel.HIGH -> {
                severityHighCard.isSelected = true
                severityMessageCard.visibility = View.VISIBLE
                severityMessageText.text = "Severe condition. Immediate medical attention required."
                severityMessageCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.severity_high_bg))
            }

            null -> severityMessageCard.visibility = View.GONE
        }
    }

    private fun updateHeaderAccent() {
        val color = when (selectedSeverity) {
            SeverityLevel.LOW -> R.color.severity_low
            SeverityLevel.MEDIUM -> R.color.severity_medium
            SeverityLevel.HIGH -> R.color.severity_high
            null -> R.color.jade_green
        }
        headerAccent.setBackgroundColor(ContextCompat.getColor(this, color))
    }

    private fun showEditSummaryDialog() {
        val editText = EditText(this).apply {
            setText(summary)
            setPadding(48, 32, 48, 32)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Edit Summary")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                summary = editText.text.toString()
                summaryText.text = summary
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun regenerateSummary() {
        summary = "AI-generated summary: Patient shows improved skin condition. Continue regimen. Prognosis positive."
        summaryText.text = summary
        Toast.makeText(this, "Summary regenerated with AI", Toast.LENGTH_SHORT).show()
    }

    private fun generateAISuggestion() {
        val suggestion = when (selectedSeverity) {
            SeverityLevel.LOW -> "Continue topical treatment. Maintain routine."
            SeverityLevel.MEDIUM -> "Add oral meds. Monitor progress bi-weekly."
            SeverityLevel.HIGH -> "Immediate intervention. Systemic therapy required."
            null -> {
                Toast.makeText(this, "Select severity first", Toast.LENGTH_SHORT).show()
                return
            }
        }
        doctorNotesEditText.setText(suggestion)
        doctorNotes = suggestion
    }

    private fun showAddTreatmentDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_treatment, null)
        val titleEditText = dialogView.findViewById<TextInputEditText>(R.id.titleEditText)
        val descriptionEditText = dialogView.findViewById<TextInputEditText>(R.id.descriptionEditText)
        val typeSpinner = dialogView.findViewById<Spinner>(R.id.typeSpinner)

        MaterialAlertDialogBuilder(this)
            .setTitle("Add Treatment Item")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = titleEditText.text.toString()
                val description = descriptionEditText.text.toString()
                val type = TreatmentType.values()[typeSpinner.selectedItemPosition]
                if (title.isNotBlank()) {
                    treatmentItems.add(
                        TreatmentItem(System.currentTimeMillis().toString(), type, title, description)
                    )
                    treatmentAdapter.notifyDataSetChanged()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteTreatmentItem(item: TreatmentItem) {
        treatmentItems.remove(item)
        treatmentAdapter.notifyDataSetChanged()
    }

    private fun savePlan() {
        Toast.makeText(this, "Plan saved successfully!", Toast.LENGTH_SHORT).show()
    }

    private fun shareWithPatient() {
        Toast.makeText(this, "Plan shared with patient", Toast.LENGTH_SHORT).show()
    }

    private fun declarePatientHealed() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Confirm")
            .setMessage("Declare this patient as healed?")
            .setPositiveButton("Yes") { _, _ ->
                Toast.makeText(this, "Patient marked recovered", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

// Data Classes
data class TreatmentItem(
    val id: String,
    val type: TreatmentType,
    val title: String,
    val description: String
)

enum class TreatmentType {
    MEDICATION,
    LIFESTYLE,
    FOLLOW_UP
}

enum class SeverityLevel {
    LOW,
    MEDIUM,
    HIGH
}

