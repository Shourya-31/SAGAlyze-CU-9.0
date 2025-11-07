package com.sagalyze.app

import android.animation.ValueAnimator
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sagalyze.R
import com.example.sagalyze.app.Patient
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.search.SearchBar
import com.google.android.material.textfield.TextInputEditText
import android.content.Intent
import com.example.sagalyze.ui.PatientAddActivity
import com.example.sagalyze.app.PatientAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var patientAdapter: PatientAdapter
    private lateinit var searchInput: TextInputEditText
    private lateinit var chipAge: Chip
    private lateinit var chipGender: Chip
    private lateinit var chipCondition: Chip
    private lateinit var chipSort: Chip

    private lateinit var fabAddPatient: FloatingActionButton

    private lateinit var backgroundView: View

    private val allPatients = listOf(
        Patient(1, "Sarah Mitchell", 34, "Female", "Psoriasis", bloodGroup = "+O"),
        Patient(2, "James Anderson", 42, "Male", "Eczema", bloodGroup = "+AB"),
        Patient(3, "Emily Rodriguez", 28, "Female", "Acne Vulgaris", bloodGroup = "+B"),
        Patient(4, "Michael Chen", 55, "Male", "Melanoma Screening", bloodGroup = "+A"),
        Patient(5, "Lisa Thompson", 39, "Female", "Rosacea", bloodGroup = "+O"),
        Patient(6, "David Park", 47, "Male", "Dermatitis", bloodGroup = "-B")
    )

    private var filteredPatients = allPatients.toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupRecyclerView()
        setupSearchBar()
        setupFilterChips()
        setupFAB()
        startBackgroundAnimation()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerViewPatients)
        searchInput = findViewById(R.id.searchInput)
        chipAge = findViewById(R.id.chipAge)
        chipGender = findViewById(R.id.chipGender)
        chipCondition = findViewById(R.id.chipCondition)
        chipSort = findViewById(R.id.chipSort)
        fabAddPatient = findViewById(R.id.fabAddPatient)
        backgroundView = findViewById(R.id.animatedBackground)
    }

    private fun setupRecyclerView() {
        patientAdapter = PatientAdapter(filteredPatients) { patient ->
            // Handle patient click
            Toast.makeText(
                this,
                "Opening details for ${patient.name}",
                Toast.LENGTH_SHORT
            ).show()
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = patientAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupSearchBar() {
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filterPatients(s.toString())
            }
        })
    }

    private fun setupFilterChips() {
        chipAge.setOnClickListener {
            chipAge.isChecked = !chipAge.isChecked
            if (chipAge.isChecked) {
                filteredPatients.sortBy { it.age }
            } else {
                filteredPatients = allPatients.toMutableList()
            }
            patientAdapter.updateList(filteredPatients)
            Toast.makeText(this, "Sorted by Age", Toast.LENGTH_SHORT).show()
        }

        chipGender.setOnClickListener {
            chipGender.isChecked = !chipGender.isChecked
            if (chipGender.isChecked) {
                filteredPatients.sortBy { it.gender.lowercase() }
            } else {
                filteredPatients = allPatients.toMutableList()
            }
            patientAdapter.updateList(filteredPatients)
            Toast.makeText(this, "Sorted by Gender", Toast.LENGTH_SHORT).show()
        }

        chipCondition.setOnClickListener {
            chipCondition.isChecked = !chipCondition.isChecked
            if (chipCondition.isChecked) {
                filteredPatients.sortBy { it.condition.lowercase() }
            } else {
                filteredPatients = allPatients.toMutableList()
            }
            patientAdapter.updateList(filteredPatients)
            Toast.makeText(this, "Sorted by Condition", Toast.LENGTH_SHORT).show()
        }

        chipSort.setOnClickListener {
            chipSort.isChecked = !chipSort.isChecked
            val sortedList = if (chipSort.isChecked) {
                filteredPatients.sortedBy { it.name.lowercase() }        // ✅ FIXED: use a sorted copy
            } else {
                filteredPatients.sortedByDescending { it.name.lowercase() } // ✅ FIXED
            }
            patientAdapter.updateList(sortedList.toMutableList())         // ✅ FIXED: pass a new list
            Toast.makeText(this, "Sorted by Name", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupFAB() {
        fabAddPatient.setOnClickListener {
            val intent = Intent(this, PatientAddActivity::class.java)
            startActivity(intent)
        }
    }


    private fun filterPatients(query: String) {
        filteredPatients.clear()

        if (query.isEmpty()) {
            filteredPatients.addAll(allPatients)
        } else {
            filteredPatients.addAll(
                allPatients.filter { patient ->
                    patient.name.contains(query, ignoreCase = true) ||
                            patient.age.toString().contains(query, ignoreCase = true) ||
                            patient.gender.contains(query, ignoreCase = true) ||
                            patient.condition.contains(query, ignoreCase = true)
                }
            )
        }

        // ✅ Ensure current sorting chip is respected during search
        when {
            chipAge.isChecked -> filteredPatients.sortBy { it.age }
            chipGender.isChecked -> filteredPatients.sortBy { it.gender.lowercase() }
            chipCondition.isChecked -> filteredPatients.sortBy { it.condition.lowercase() }
            chipSort.isChecked -> filteredPatients.sortBy { it.name.lowercase() }
        }

        patientAdapter.notifyDataSetChanged()
    }



    private fun startBackgroundAnimation() {
        // Animate gradient background with parallax effect
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 8000
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.interpolator = AccelerateDecelerateInterpolator()

        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            backgroundView.alpha = 0.3f + (progress * 0.3f)
        }

        animator.start()
    }

}