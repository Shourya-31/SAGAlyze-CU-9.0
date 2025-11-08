package com.sagalyze.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import com.example.sagalyze.R
import com.example.sagalyze.diagnostic.PlanActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*

class AddPatientFragment : Fragment() {

    private lateinit var etPatientName: TextInputEditText
    private lateinit var etAge: TextInputEditText
    private lateinit var spinnerGender: Spinner
    private lateinit var etContactNumber: TextInputEditText
    private lateinit var etDateOfVisit: TextInputEditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var etPrimaryComplaint: EditText
    private lateinit var etAffectedAreas: EditText
    private lateinit var etSymptomsAndDuration: EditText
    private lateinit var etPreviousTreatment: EditText
    private lateinit var etDoctorNotes: EditText
    private lateinit var tvUploadedFileName: TextView
    private lateinit var btnChooseFile: MaterialButton
    private lateinit var btnAddDiagnosisPlan: MaterialButton
    private lateinit var btnSavePatient: MaterialButton
    private lateinit var btnCancel: MaterialButton

    private var selectedImageUri: Uri? = null
    private val calendar = Calendar.getInstance()

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_patient, container, false)

        val addDiagnosisButton =
            view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_add_diagnosis_plan)

        addDiagnosisButton?.setOnClickListener {
            Toast.makeText(requireContext(), "Opening PlanActivity...", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), PlanActivity::class.java)
            startActivity(intent)
        }

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupSpinners()
        setupDatePicker()
        setupListeners()
        setCurrentDate()
    }

    private fun initializeViews(view: View) {
        etPatientName = view.findViewById(R.id.et_patient_name)
        etAge = view.findViewById(R.id.et_age)
        spinnerGender = view.findViewById(R.id.spinner_gender)
        etContactNumber = view.findViewById(R.id.et_contact_number)
        etDateOfVisit = view.findViewById(R.id.et_date_of_visit)
        spinnerCategory = view.findViewById(R.id.spinner_category)
        etPrimaryComplaint = view.findViewById(R.id.et_primary_complaint)
        etAffectedAreas = view.findViewById(R.id.et_affected_areas)
        etSymptomsAndDuration = view.findViewById(R.id.et_symptoms_and_duration)
        etPreviousTreatment = view.findViewById(R.id.et_previous_treatment)
        etDoctorNotes = view.findViewById(R.id.et_doctor_notes)
        tvUploadedFileName = view.findViewById(R.id.tv_uploaded_file_name)
        btnChooseFile = view.findViewById(R.id.btn_choose_file)
        btnAddDiagnosisPlan = view.findViewById(R.id.btn_add_diagnosis_plan)
        btnSavePatient = view.findViewById(R.id.btn_save_patient)
        btnCancel = view.findViewById(R.id.btn_cancel)
    }

    private fun setupSpinners() {
        // Gender Spinner
        val genderAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender_options,
            android.R.layout.simple_spinner_item
        )
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender.adapter = genderAdapter

        // Category Spinner
        val categoryAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.category_options,
            android.R.layout.simple_spinner_item
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter
    }

    private fun setupDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

        etDateOfVisit.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setCurrentDate() {
        updateDateInView()
    }

    private fun updateDateInView() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        etDateOfVisit.setText(dateFormat.format(calendar.time))
    }

    private fun setupListeners() {
        btnChooseFile.setOnClickListener {
            openImagePicker()
        }

        btnAddDiagnosisPlan.setOnClickListener {
            handleDiagnosisPlan()
        }

        btnSavePatient.setOnClickListener {
            savePatient()
        }

        btnCancel.setOnClickListener {
            cancelForm()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                selectedImageUri = uri
                tvUploadedFileName.text = getFileName(uri)
                tvUploadedFileName.visibility = View.VISIBLE
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        var fileName = "Unknown"
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }

    private fun handleDiagnosisPlan() {
        Toast.makeText(requireContext(), "Button Clicked — opening activity...", Toast.LENGTH_SHORT).show()

        try {
            val intent = Intent(requireContext(), PlanActivity::class.java)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "❌ Error: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }


    private fun savePatient() {
        // Validate inputs
        if (!validateInputs()) {
            return
        }

        // Create patient data object
        val patientData = PatientData(
            patientName = etPatientName.text.toString(),
            age = etAge.text.toString().toIntOrNull() ?: 0,
            gender = spinnerGender.selectedItem.toString(),
            contactNumber = etContactNumber.text.toString(),
            dateOfVisit = etDateOfVisit.text.toString(),
            category = spinnerCategory.selectedItem.toString(),
            primaryComplaint = etPrimaryComplaint.text.toString(),
            affectedAreas = etAffectedAreas.text.toString(),
            symptomsAndDuration = etSymptomsAndDuration.text.toString(),
            previousTreatment = etPreviousTreatment.text.toString(),
            doctorNotes = etDoctorNotes.text.toString(),
            imageUri = selectedImageUri?.toString()
        )

        // Save to database or send to server
        Toast.makeText(requireContext(), "Patient saved successfully!", Toast.LENGTH_SHORT).show()

        // Clear form or navigate back
        clearForm()
    }

    private fun validateInputs(): Boolean {
        if (etPatientName.text.isNullOrBlank()) {
            etPatientName.error = "Patient name is required"
            return false
        }

        if (etAge.text.isNullOrBlank()) {
            etAge.error = "Age is required"
            return false
        }

        return true
    }

    private fun cancelForm() {
        requireActivity().onBackPressed()
    }

    private fun clearForm() {
        etPatientName.text?.clear()
        etAge.text?.clear()
        spinnerGender.setSelection(0)
        etContactNumber.text?.clear()
        spinnerCategory.setSelection(0)
        etPrimaryComplaint.text?.clear()
        etAffectedAreas.text?.clear()
        etSymptomsAndDuration.text?.clear()
        etPreviousTreatment.text?.clear()
        etDoctorNotes.text?.clear()
        selectedImageUri = null
        tvUploadedFileName.visibility = View.GONE
        setCurrentDate()
    }
}

data class PatientData(
    val patientName: String,
    val age: Int,
    val gender: String,
    val contactNumber: String,
    val dateOfVisit: String,
    val category: String,
    val primaryComplaint: String,
    val affectedAreas: String,
    val symptomsAndDuration: String,
    val previousTreatment: String,
    val doctorNotes: String,
    val imageUri: String?
)
