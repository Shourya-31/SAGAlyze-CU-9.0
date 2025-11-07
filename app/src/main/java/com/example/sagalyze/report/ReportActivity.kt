package com.example.sagalyze.report

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sagalyze.R
import com.example.sagalyze.databinding.ActivityReportBinding
import com.example.sagalyze.report.adapters.PastVisitsAdapter
import com.example.sagalyze.report.models.Visit
import com.example.sagalyze.report.utils.ToastHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import java.io.File
import java.io.FileOutputStream
import java.util.*

class ReportActivity : AppCompatActivity() {

    // UI Components
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var recyclerPastVisits: RecyclerView

    // File & state
    private var nextVisitDate: Date? = null
    private var uploadedFileUri: Uri? = null

    // Adapters
    private lateinit var pastVisitsAdapter: PastVisitsAdapter

    // File picker launcher
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                handleFileSelection(uri)
            }
        }
    }

    // Camera launcher
    private var capturedImageFile: File? = null

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val bitmap = result.data?.extras?.get("data") as Bitmap

            // Save locally
            capturedImageFile =File(cacheDir, "lesion_${System.currentTimeMillis()}.jpg")
            FileOutputStream(capturedImageFile!!).use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }

            // Show preview
            val includeNewReport = findViewById<View>(R.id.includeNewReport)
            val ivPreview = includeNewReport.findViewById<ImageView>(R.id.ivPreview)
            ivPreview.setImageBitmap(bitmap)
            ivPreview.visibility = View.VISIBLE
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        // --- Retrieve intent extras safely ---
        val patientName = intent.getStringExtra("PATIENT_NAME") ?: "Unknown"
        val patientId = intent.getStringExtra("PATIENT_ID") ?: "N/A"
        val patientCondition = intent.getStringExtra("PATIENT_CONDITION") ?: "N/A"

        val ageFromString = intent.getStringExtra("PATIENT_AGE")
        val ageFromInt = if (intent.hasExtra("PATIENT_AGE")) {
            try {
                intent.getIntExtra("PATIENT_AGE", -1)
            } catch (e: Exception) {
                -1
            }
        } else {
            -1
        }
        val patientAge = when {
            !ageFromString.isNullOrBlank() -> ageFromString
            ageFromInt >= 0 -> ageFromInt.toString()
            else -> "N/A"
        }

        val patientGender = intent.getStringExtra("PATIENT_GENDER") ?: "N/A"
        val patientBloodGroup = intent.getStringExtra("PATIENT_BLOOD_GROUP") ?: "N/A"

        // --- Initialize views ---
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        setupToolbar()
        setupNavigationDrawer()

        setupPatientHeaderDirect(
            name = patientName,
            id = patientId,
            condition = patientCondition,
            age = patientAge,
            gender = patientGender,
            bloodGroup = patientBloodGroup
        )

        setupBackButton()
        setupPastVisits()
        setupNewReportCard()
        setupActionButtons()

        // Handle back press dispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun setupToolbar() {
        findViewById<View>(R.id.ivMenuButton).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        findViewById<View>(R.id.ivLogoutButton).setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun setupNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_patients -> {
                    ToastHelper.showInfo(this, "Navigating to Patient Records...")
                }
                R.id.nav_dashboard -> {
                    ToastHelper.showInfo(this, "Navigating to Dashboard...")
                }
                R.id.nav_settings -> {
                    ToastHelper.showInfo(this, "Opening Settings...")
                }
                R.id.nav_logout -> {
                    showLogoutDialog()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun setupPatientHeaderDirect(
        name: String,
        id: String,
        condition: String,
        age: String,
        gender: String,
        bloodGroup: String
    ) {
        findViewById<TextView>(R.id.tvPatientName)?.text = name
        findViewById<TextView>(R.id.tvPatientId)?.text = getString(R.string.patient_id_label) + " " + id
        findViewById<TextView>(R.id.tvCondition)?.text = condition
        findViewById<TextView>(R.id.tvAge)?.text = age
        findViewById<TextView>(R.id.tvGender)?.text = gender
        findViewById<TextView>(R.id.tvBloodGroup)?.text = bloodGroup
        findViewById<TextView>(R.id.tvDetailLabel)?.text = "Full Name"
        findViewById<TextView>(R.id.tvDetailValue)?.text = name
        findViewById<TextView>(R.id.tvPatientIdDetail)?.text = id
    }

    private fun setupBackButton() {
        findViewById<View>(R.id.btnBackToRecords).setOnClickListener {
            ToastHelper.showInfo(this, getString(R.string.toast_navigation))
        }
    }

    private fun setupPastVisits() {
        val pastVisits = Visit.getSampleVisits()
        pastVisitsAdapter = PastVisitsAdapter(pastVisits) { visit ->
            handleViewReport(visit)
        }

        recyclerPastVisits = findViewById(R.id.rvPastVisits)
        recyclerPastVisits.layoutManager = LinearLayoutManager(this)
        recyclerPastVisits.adapter = pastVisitsAdapter
    }

    private fun setupNewReportCard() {
        val includeNewReport = findViewById<View>(R.id.includeNewReport)
        val uploadArea = includeNewReport.findViewById<View>(R.id.uploadArea)
        val uploadPrompt = includeNewReport.findViewById<View>(R.id.uploadPrompt)
        val fileSelectedView = includeNewReport.findViewById<View>(R.id.fileSelectedView)
        val btnRemoveFile = includeNewReport.findViewById<MaterialButton>(R.id.btnRemoveFile)

        // Upload Area
        uploadArea.setOnClickListener {
            openFilePicker()
        }

        // Remove File
        btnRemoveFile.setOnClickListener {
            uploadedFileUri = null
            uploadPrompt.visibility = View.VISIBLE
            fileSelectedView.visibility = View.GONE
            ToastHelper.showInfo(this, "File removed")
        }

        // Generate Report
        includeNewReport.findViewById<View>(R.id.btnGenerateReport)?.setOnClickListener {
            handleGenerateReport()
        }

        // Camera Button
        val btnClickToUpload = includeNewReport.findViewById<MaterialButton>(R.id.btnClickToUpload)
        btnClickToUpload?.setOnClickListener {
            if (checkCameraPermission()) {
                openCamera()
            } else {
                requestCameraPermission()
            }
        }
    }

    private fun setupActionButtons() {
        findViewById<View>(R.id.btnSaveInfo).setOnClickListener {
            val name = findViewById<TextView>(R.id.tvPatientName)?.text ?: "patient"
            ToastHelper.showSuccess(this, getString(R.string.toast_saved, name))
        }

        findViewById<View>(R.id.btnClearAll).setOnClickListener {
            handleClearAll()
        }

        findViewById<View>(R.id.btnDownloadPdf).setOnClickListener {
            generatePdf()
        }

        findViewById<View>(R.id.btnSendToPatient).setOnClickListener {
            sharePdf()
        }


    }

    private fun sharePdf() {
        val pdfFile = File(getExternalFilesDir(null), "last_report.pdf")
        if (!pdfFile.exists()) {
            ToastHelper.showError(this, "Generate the PDF first")
            return
        }

        val uri = FileProvider.getUriForFile(
            this,
            "$packageName.provider",
            pdfFile
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Send report to patient"))
    }


    private fun generatePdf() {
        if (capturedImageFile == null) {
            ToastHelper.showError(this, "No image captured")
            return
        }

        val reportTitle = findViewById<EditText>(R.id.etReportTitle)?.text.toString()
        val findings = findViewById<EditText>(R.id.etClinicalFindings)?.text.toString()

        val pdfFile = File(getExternalFilesDir(null), "Report_${System.currentTimeMillis()}.pdf")
        val document =PdfDocument()

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val paint =Paint()
        paint.textSize = 18f
        paint.isFakeBoldText = true

        // Title
        canvas.drawText("Dermatology Report", 50f, 50f, paint)

        paint.textSize = 14f
        paint.isFakeBoldText = false
        canvas.drawText("Report Title: $reportTitle", 50f, 100f, paint)
        canvas.drawText("Findings: $findings", 50f, 140f, paint)

        // Attach Image
        val bitmap = BitmapFactory.decodeFile(capturedImageFile!!.absolutePath)
        val scaled = Bitmap.createScaledBitmap(bitmap, 400, 400, true)
        canvas.drawBitmap(scaled, 50f, 180f, null)

        document.finishPage(page)
        document.writeTo(FileOutputStream(pdfFile))
        document.close()

        ToastHelper.showSuccess(this, "PDF saved: ${pdfFile.path}")
    }


    private fun openFilePicker() {
        val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(this, permission)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_REQUEST_CODE)
            return
        }

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png", "image/heic"))
        }
        filePickerLauncher.launch(intent)
    }

    private fun handleFileSelection(uri: Uri) {
        uploadedFileUri = uri

        val includeNewReport = findViewById<View>(R.id.includeNewReport)
        val uploadPrompt = includeNewReport.findViewById<View>(R.id.uploadPrompt)
        val fileSelectedView = includeNewReport.findViewById<View>(R.id.fileSelectedView)
        val tvFileName = includeNewReport.findViewById<TextView>(R.id.tvFileNameSelected)
        val tvFileSize = includeNewReport.findViewById<TextView>(R.id.tvFileSizeSelected)

        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (cursor.moveToFirst()) {
                val fileName = cursor.getString(nameIndex)
                val fileSize = cursor.getLong(sizeIndex)

                uploadPrompt.visibility = View.GONE
                fileSelectedView.visibility = View.VISIBLE
                tvFileName.text = fileName
                tvFileSize.text = String.format("%.2f MB", fileSize / (1024.0 * 1024.0))
            }
        }

        ToastHelper.showSuccess(this, "File selected successfully!")
    }

    private fun handleGenerateReport() {
        val reportTitle = findViewById<EditText>(R.id.etReportTitle)?.text?.toString() ?: ""

        if (reportTitle.isBlank()) {
            ToastHelper.showError(this, getString(R.string.toast_report_title_missing))
            return
        }

        if (uploadedFileUri == null) {
            ToastHelper.showError(this, getString(R.string.toast_photo_missing))
            return
        }

        ToastHelper.showSuccess(this, getString(R.string.toast_report_generated))
    }

    private fun handleClearAll() {
        nextVisitDate = null
        uploadedFileUri = null
        findViewById<EditText>(R.id.etReportTitle)?.setText("")
        findViewById<EditText>(R.id.etClinicalFindings)?.setText("")

        findViewById<View>(R.id.uploadPrompt)?.visibility = View.VISIBLE
        findViewById<View>(R.id.fileSelectedView)?.visibility = View.GONE

        ToastHelper.showInfo(this, getString(R.string.toast_cleared))
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(cameraIntent)
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        }
    }

    private fun handleViewReport(visit: Visit) {
        ToastHelper.showInfo(
            this,
            getString(R.string.toast_opening_report, visit.date)
        )
    }

    private fun showLogoutDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                ToastHelper.showInfo(this, "Logging out...")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }
}
