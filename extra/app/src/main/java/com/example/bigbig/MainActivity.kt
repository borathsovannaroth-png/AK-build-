package com.example.bigbig

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvServiceStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvServiceStatus = findViewById(R.id.tvServiceStatus)

        showCreditPopup()

        findViewById<Button>(R.id.btnOption1).setOnClickListener {
            Toast.makeText(this, "Preset 1 selected", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnOption2).setOnClickListener {
            Toast.makeText(this, "Preset 2 selected", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnReset).setOnClickListener {
            Toast.makeText(this, "Reset to default", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnRefresh).setOnClickListener {
            checkShizukuInstalled()
        }

        findViewById<Button>(R.id.btnCheckService).setOnClickListener {
            val launchIntent = packageManager.getLaunchIntentForPackage("moe.shizuku.privileged.api")
            if (launchIntent != null) {
                startActivity(launchIntent)
            } else {
                Toast.makeText(this, "Shizuku app is not installed on this device", Toast.LENGTH_SHORT).show()
            }
        }

        checkShizukuInstalled()
    }

    private fun showCreditPopup() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_credit, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogView.findViewById<Button>(R.id.btnClosePopup).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun checkShizukuInstalled() {
        val isInstalled = try {
            packageManager.getPackageInfo("moe.shizuku.privileged.api", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

        if (isInstalled) {
            tvServiceStatus.text = "Shizuku Package: Installed"
            tvServiceStatus.setTextColor(0xFF4ADE80.toInt())
        } else {
            tvServiceStatus.text = "Shizuku Package: Not Installed"
            tvServiceStatus.setTextColor(0xFFF87171.toInt())
        }
    }
}
