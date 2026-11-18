package com.akula.gm

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var pm: PackageManager
    private lateinit var gameList: LinearLayout
    private lateinit var shizukuStatus: TextView
    private lateinit var ramInfo: TextView

    private val handler = Handler(Looper.getMainLooper())

    data class Game(val name: String, val pkg: String)
    var installed = false
    var installed2 = false

    private val games = listOf(
        Game("Free Fire", "com.dts.freefireth"),
        Game("PUBG Mobile", "com.tencent.ig"),
        Game("Call of Duty Mobile", "com.activision.callofduty.shooter"),
        Game("Mobile Legends", "com.mobile.legends"),
        Game("eFootball", "jp.konami.pesam")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pm = packageManager

        shizukuStatus = findViewById(R.id.shizukuStatus)
        ramInfo = findViewById(R.id.ramInfo)
        gameList = findViewById(R.id.gameList)

        findViewById<Button>(R.id.connectBtn).setOnClickListener {
            openShizuku()
        }

        checkShizuku()
        buildGameList()

        // Live RAM updater
        val ramRunnable = object : Runnable {
            override fun run() {
                ramInfo.text = "RAM: GameBooster.freeRamMb(this@MainActivity)MBfree/{GameBooster.freeRamMb(this@MainActivity)} MB free /GameBooster.freeRamMb(this@MainActivity)MBfree/{GameBooster.totalRamMb(this@MainActivity)} MB total"
                handler.postDelayed(this, 2000)
            }
        }
        handler.post(ramRunnable)
    }

    private fun checkShizuku() {
        val ok = ShizukuHelper.isConnected()
        shizukuStatus.text =
            if (ok) "Shizuku Connected ✅" else "Shizuku Status: Not yet ❌"
        shizukuStatus.setTextColor(
            if (ok) 0xFF4ADE80.toInt() else 0xFFFF8888.toInt()
        )
        if (ok) ShizukuHelper.requestPermission()
    }

    private fun openShizuku() {
        try {
            startActivity(
                Intent("moe.shizuku.manager.intent.START")
                    .setPackage("moe.shizuku.privileged.api")
            )
        } catch (e: Exception) {
            Toast.makeText(this, "Shizuku app not installed!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isInstalled(pkg: String): Boolean {
        return try {
            pm.getPackageInfo(pkg, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun buildGameList() {
        gameList.removeAllViews()
        for (g in games) {
            val ok = isInstalled(g.pkg)

            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setBackgroundResource(R.drawable.glass_bg)
                setPadding(24, 28, 24, 28)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = 16 }
            }

            val icon = TextView(this).apply {
                text = "🎮"
                textSize = 22f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { marginEnd = 24 }
            }

            val info = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                )
            }
            info.addView(TextView(this@MainActivity).apply {
                text = g.name
                setTextColor(0xFFFFFFFF.toInt())
                textSize = 16f
            })
            info.addView(TextView(this@MainActivity).apply {
                text = if (ok) "✅ Installed" else "❌ Not installed"
                setTextColor(
                    if (ok) 0xFF4ADE80.toInt() else 0xFFFF8888.toInt()
                )
                textSize = 12f
            })

            val btn = TextView(this@MainActivity).apply {
                text = "▶"
                textSize = 20f
                setTextColor(0xFF8FB8FF.toInt())
                setPadding(32, 16, 32, 16)
                alpha = if (ok) 1f else 0.3f
            }
            if (ok) {
                btn.setOnClickListener { launchGame(g) }
            }

            row.addView(icon)
            row.addView(info)
            row.addView(btn)
            gameList.addView(row)
        }
    }

    private fun launchGame(g: Game) {
        if (!ShizukuHelper.isConnected()) {
            Toast.makeText(this, "Connect Shizuku first!", Toast.LENGTH_SHORT).show()
            openShizuku()
            return
        }

        GameBooster.refreshGame(this, g.pkg)
        GameBooster.clearRam(this)

        val launch = pm.getLaunchIntentForPackage(g.pkg)
        if (launch != null) {
            startActivity(launch)
        } else {
            Toast.makeText(this, "Cannot launch ${g.name}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        checkShizuku()
        buildGameList()
    }
}
