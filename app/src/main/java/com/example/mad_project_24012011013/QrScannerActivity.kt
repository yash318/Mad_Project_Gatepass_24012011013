package com.example.mad_project_24012011013

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class QrScannerActivity : AppCompatActivity() {

    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result ->

        if (result.contents != null) {

            val gatePassId = result.contents

            Toast.makeText(
                this,
                "QR Scanned Successfully",
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent(
                this,
                ScannedGatePassActivity::class.java
            )

            intent.putExtra("gatePassId", gatePassId)

            startActivity(intent)

        } else {

            Toast.makeText(
                this,
                "Scan cancelled",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_qr_scanner)

        val btnStartScan =
            findViewById<MaterialButton>(R.id.btnStartScan)

        val btnBack =
            findViewById<MaterialButton>(R.id.btnBack)

        btnStartScan.setOnClickListener {

            val options = ScanOptions()

            options.setPrompt("Scan HostelPass QR Code")
            options.setBeepEnabled(true)
            options.setOrientationLocked(false)

            barcodeLauncher.launch(options)
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}