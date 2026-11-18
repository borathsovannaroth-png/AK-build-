package com.akula.gm

import android.content.Context
import android.os.StatFs

object GameBooster {

    private val killed = mutableSetOf<String>()

    fun freeRamMb(context: Context): Long {
        val stat = StatFs("/proc/meminfo")
        return try {
            val mi = android.os.Debug.MemoryInfo()
            mi.availMem / (1024 * 1024)
        } catch (e: Exception) {
            0L
        }
    }

    fun totalRamMb(context: Context): Long {
        return try {
            val mi = android.app.ActivityManager.MemoryInfo()
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
            am.memoryInfo.totalMem / (1024 * 1024)
        } catch (e: Exception) {
            0L
        }
    }

    fun refreshGame(context: Context, pkg: String) {
        try {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
            am.killBackgroundProcesses(pkg)
        } catch (_: Exception) {}
    }

    fun clearRam(context: Context) {
        try {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
            val running = am.runningAppProcesses ?: return
            for (p in running) {
                if (p.processName != context.packageName) {
                    am.killBackgroundProcesses(p.processName)
                }
            }
        } catch (_: Exception) {}
    }
}
