package com.akula.gm

import android.content.Context
import android.app.ActivityManager

object GameBooster {

    fun freeRamMb(context: Context): Long {
        return try {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val mi = ActivityManager.MemoryInfo()
            am.getMemoryInfo(mi)
            mi.availMem / (1024 * 1024)
        } catch (e: Exception) {
            0L
        }
    }

    fun totalRamMb(context: Context): Long {
        return try {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val mi = ActivityManager.MemoryInfo()
            am.getMemoryInfo(mi)
            mi.totalMem / (1024 * 1024)
        } catch (e: Exception) {
            0L
        }
    }

    fun refreshGame(context: Context, pkg: String) {
        try {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            am.killBackgroundProcesses(pkg)
        } catch (_: Exception) {}
    }

    fun clearRam(context: Context) {
        try {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val running = am.runningAppProcesses ?: return
            for (p in running) {
                if (p.processName != context.packageName) {
                    am.killBackgroundProcesses(p.processName)
                }
            }
        } catch (_: Exception) {}
    }
}
