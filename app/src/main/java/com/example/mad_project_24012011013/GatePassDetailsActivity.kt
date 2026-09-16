package com.example.mad_project_24012011013

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import android.graphics.Bitmap
import android.widget.ImageView
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter

class GatePassDetailsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gate_pass_details)

        db = FirebaseFirestore.getInstance()

        val tvStudentName =
            findViewById<TextView>(R.id.tvStudentName)

        val tvPlace =
            findViewById<TextView>(R.id.tvPlace)

        val tvReason =
            findViewById<TextView>(R.id.tvReason)

        val tvDescription =
            findViewById<TextView>(R.id.tvDescription)

        val tvExit =
            findViewById<TextView>(R.id.tvExit)

        val tvReturn =
            findViewById<TextView>(R.id.tvReturn)

        val tvParentApproval =
            findViewById<TextView>(R.id.tvParentApproval)

        val tvRectorApproval =
            findViewById<TextView>(R.id.tvRectorApproval)

        val tvStatus =
            findViewById<TextView>(R.id.tvStatus)

        val btnBack =
            findViewById<MaterialButton>(R.id.btnBack)

        val imgQrCode = findViewById<ImageView>(R.id.imgQrCode)

        // Get Firestore document ID
        val gatePassId =
            intent.getStringExtra("gatePassId")

        if (gatePassId.isNullOrEmpty()) {

            Toast.makeText(
                this,
                "Gate pass not found",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        // Load gate pass from Firestore
        db.collection("gatePasses")
            .document(gatePassId)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val studentName =
                        document.getString("studentName") ?: ""

                    val place =
                        document.getString("place") ?: ""

                    val reason =
                        document.getString("reason") ?: ""

                    val description =
                        document.getString("description") ?: ""

                    val exitDate =
                        document.getString("exitDate") ?: ""

                    val exitTime =
                        document.getString("exitTime") ?: ""

                    val returnDate =
                        document.getString("returnDate") ?: ""

                    val returnTime =
                        document.getString("returnTime") ?: ""

                    val parentApproval =
                        document.getBoolean("parentApproval") ?: false

                    val rectorApproval =
                        document.getBoolean("rectorApproval") ?: false

                    val status =
                        document.getString("status") ?: "PENDING"

                    tvStudentName.text =
                        "Student: $studentName"

                    tvPlace.text =
                        "Place: $place"

                    tvReason.text =
                        "Reason: $reason"

                    tvDescription.text =
                        "Description: $description"

                    tvExit.text =
                        "Exit: $exitDate $exitTime"

                    tvReturn.text =
                        "Return: $returnDate $returnTime"

                    tvParentApproval.text =
                        if (parentApproval) {
                            "Parent Approval: Approved"
                        } else {
                            "Parent Approval: Pending"
                        }

                    tvRectorApproval.text =
                        if (rectorApproval) {
                            "Rector Approval: Approved"
                        } else {
                            "Rector Approval: Pending"
                        }

                    tvStatus.text =
                        "Status: $status"
                    if (status == "APPROVED") {

                        imgQrCode.visibility = ImageView.VISIBLE

                        val qrData = gatePassId

                        val bitMatrix = MultiFormatWriter().encode(
                            qrData,
                            BarcodeFormat.QR_CODE,
                            600,
                            600
                        )

                        val bitmap = Bitmap.createBitmap(
                            600,
                            600,
                            Bitmap.Config.RGB_565
                        )

                        for (x in 0 until 600) {
                            for (y in 0 until 600) {
                                bitmap.setPixel(
                                    x,
                                    y,
                                    if (bitMatrix[x, y])
                                        android.graphics.Color.BLACK
                                    else
                                        android.graphics.Color.WHITE
                                )
                            }
                        }

                        imgQrCode.setImageBitmap(bitmap)

                    } else {
                        imgQrCode.visibility = ImageView.GONE
                    }

                } else {

                    Toast.makeText(
                        this,
                        "Gate pass not found",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Unable to load gate pass",
                    Toast.LENGTH_LONG
                ).show()
            }

        btnBack.setOnClickListener {
            finish()
        }
    }
}