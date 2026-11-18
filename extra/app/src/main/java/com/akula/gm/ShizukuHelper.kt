package com.akula.gm

import rikka.shizuku.Shizuku

object ShizukuHelper {

    fun isConnected(): Boolean {
        return try {
            Shizuku.pingBinder() && Shizuku.checkSelfPermission() == Shizuku.PERMISSION_GRANTED
        } catch (e: Exception) {
            false
        }
    }

    fun requestPermission() {
        try {
            if (Shizuku.checkSelfPermission() != Shizuku.PERMISSION_GRANTED) {
                Shizuku.requestPermission(0)
            }
        } catch (_: Exception) { }
    }

    fun exec(cmd: String): String? {
        return try {
            Shizuku.newProcess(arrayOf("sh", "-c", cmd), null, null)
                .inputStream.bufferedReader().readText().trim()
        } catch (e: Exception) {
            try {
                Runtime.getRuntime().exec(arrayOf("sh", "-c", cmd))
                    .inputStream.bufferedReader().readText().trim()
            } catch (e2: Exception) { null }
        }
    }
}
