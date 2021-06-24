package com.example.oxybite.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.oxybite.OXYBITE_SHARED_PREF
import com.example.oxybite.R

class DailyResetWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        reset()
        return Result.success()
    }

    private fun reset() {
        val sharedpref = context.getSharedPreferences(
            OXYBITE_SHARED_PREF,
            Context.MODE_PRIVATE
        )
        sharedpref.edit().apply {
            putInt(context.getString(R.string.glass_count), 0)
            apply()
        }

    }
}