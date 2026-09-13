package com.example.mad_project_24012011013

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ApplyGatePassActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apply_gate_pass)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val etExitDate =
            findViewById<TextInputEditText>(R.id.etExitDate)

        val etExitTime =
            findViewById<TextInputEditText>(R.id.etExitTime)

        val etReturnDate =
            findViewById<TextInputEditText>(R.id.etReturnDate)

        val etReturnTime =
            findViewById<TextInputEditText>(R.id.etReturnTime)

        val etPlace =
            findViewById<TextInputEditText>(R.id.etPlace)

        val etReason =
            findViewById<TextInputEditText>(R.id.etReason)

        val etDescription =
            findViewById<TextInputEditText>(R.id.etDescription)

        val btnSubmitGatePass =
            findViewById<MaterialButton>(R.id.btnSubmitGatePass)

        val btnBack =
            findViewById<MaterialButton>(R.id.btnBack)

        // Exit date picker
        etExitDate.setOnClickListener {
            showDatePicker(etExitDate)
        }

        // Exit time picker
        etExitTime.setOnClickListener {
            showTimePicker(etExitTime)
        }

        // Return date picker
        etReturnDate.setOnClickListener {
            showDatePicker(etReturnDate)
        }

        // Return time picker
        etReturnTime.setOnClickListener {
            showTimePicker(etReturnTime)
        }

        // Submit gate pass
        btnSubmitGatePass.setOnClickListener {

            val exitDate = etExitDate.text.toString().trim()
            val exitTime = etExitTime.text.toString().trim()
            val returnDate = etReturnDate.text.toString().trim()
            val returnTime = etReturnTime.text.toString().trim()
            val place = etPlace.text.toString().trim()
            val reason = etReason.text.toString().trim()
            val description = etDescription.text.toString().trim()

            // Check empty fields
            if (exitDate.isEmpty() ||
                exitTime.isEmpty() ||
                returnDate.isEmpty() ||
                returnTime.isEmpty() ||
                place.isEmpty() ||
                reason.isEmpty() ||
                description.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // Convert date and time into Date objects
            val dateFormat =
                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

            dateFormat.isLenient = false

            val exitDateTime = try {
                dateFormat.parse("$exitDate $exitTime")
            } catch (e: Exception) {
                null
            }

            val returnDateTime = try {
                dateFormat.parse("$returnDate $returnTime")
            } catch (e: Exception) {
                null
            }

            if (exitDateTime == null || returnDateTime == null) {
                Toast.makeText(
                    this,
                    "Please select valid date and time",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // Exit time cannot be in the past
            if (exitDateTime.before(Calendar.getInstance().time)) {
                Toast.makeText(
                    this,
                    "Exit date and time cannot be in the past",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // Return must be after exit
            if (!returnDateTime.after(exitDateTime)) {
                Toast.makeText(
                    this,
                    "Return time must be after exit time",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val currentUser = auth.currentUser

            if (currentUser == null) {
                Toast.makeText(
                    this,
                    "Please login again",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // Prevent multiple clicks
            btnSubmitGatePass.isEnabled = false

            val studentId = currentUser.uid

            // Get student profile
            db.collection("users")
                .document(studentId)
                .get()
                .addOnSuccessListener { document ->

                    val studentName =
                        document.getString("name") ?: ""

                    val parentId =
                        document.getString("parentId") ?: ""

                    val rectorId =
                        document.getString("rectorId") ?: ""

                    // Gate pass data
                    val gatePass = hashMapOf(
                        "studentId" to studentId,
                        "studentName" to studentName,

                        "parentId" to parentId,
                        "rectorId" to rectorId,

                        "exitDate" to exitDate,
                        "exitTime" to exitTime,

                        "returnDate" to returnDate,
                        "returnTime" to returnTime,

                        "place" to place,
                        "reason" to reason,
                        "description" to description,

                        "parentApproval" to false,
                        "rectorApproval" to false,

                        "status" to "PENDING",

                        "qrCode" to "",

                        "createdAt" to FieldValue.serverTimestamp()
                    )

                    // Save to Firestore
                    db.collection("gatePasses")
                        .add(gatePass)
                        .addOnSuccessListener {

                            Toast.makeText(
                                this,
                                "Gate pass request submitted successfully",
                                Toast.LENGTH_LONG
                            ).show()

                            finish()
                        }
                        .addOnFailureListener {

                            btnSubmitGatePass.isEnabled = true

                            Toast.makeText(
                                this,
                                "Failed to submit gate pass",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
                .addOnFailureListener {

                    btnSubmitGatePass.isEnabled = true

                    Toast.makeText(
                        this,
                        "Unable to load student profile",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }

        // Back button
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun showDatePicker(editText: TextInputEditText) {

        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->

                val date = String.format(
                    "%02d/%02d/%04d",
                    selectedDay,
                    selectedMonth + 1,
                    selectedYear
                )

                editText.setText(date)
            },
            year,
            month,
            day
        )

        datePicker.show()
    }

    private fun showTimePicker(editText: TextInputEditText) {

        val calendar = Calendar.getInstance()

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->

                val time = String.format(
                    "%02d:%02d",
                    selectedHour,
                    selectedMinute
                )

                editText.setText(time)
            },
            hour,
            minute,
            true
        )

        timePicker.show()
    }
}