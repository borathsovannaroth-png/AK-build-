package com.akula.gm

import android.app.ActivityManager
import android.content.Context

object GameBooster {

    fun freeRamMb(ctx: Context): Long {
        val am = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        return mi.availMem / (1024 * 1024)
    }

    fun totalRamMb(ctx: Context): Long {
        val am = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        return mi.totalMem / (1024 * 1024)
    }

    fun clearRam(ctx: Context) {
        val am = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        try {
            for (p in am.runningAppProcesses) {
                if (p.importance >= ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    am.killBackgroundProcesses(p.processName)
                }
            }
        } catch (_: Exception) { }
        ShizukuHelper.exec("sync; echo 3 > /proc/sys/vm/drop_caches")
    }

    fun refreshGame(ctx: Context, pkg: String) {
        clearRam(ctx)
        ShizukuHelper.exec("am kill-all")
        ShizukuHelper.exec("cmd activity kill-all")
        ShizukuHelper.exec("settings put global window_animation_scale 0")
        ShizukuHelper.exec("settings put global transition_animation_scale 0")
        ShizukuHelper.exec("settings put global animator_duration_scale 0")
        ShizukuHelper.exec("cmd power set-fixed-performance-mode-enabled true")
        ShizukuHelper.exec("cmd activity set-inactive $pkg false")
    }

    fun clearCache(ctx: Context) {
        ShizukuHelper.exec("pm trim-caches 999999999999")
        clearRam(ctx)
    }

    fun clearBackground(ctx: Context) {
        ShizukuHelper.exec("am kill-all")
    }
}
