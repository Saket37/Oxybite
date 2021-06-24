package com.example.oxybite.settings

import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.oxybite.R
import com.example.oxybite.worker.NotificationWorker
import java.time.Duration

const val NOTIFICATION_WORKER_TAG = "reminderTag"

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        val reminderIntervalKey = getString(R.string.key_interval)

        val switchNotification: SwitchPreference? =
            findPreference(getString(R.string.key_notification))
        val reminderInterval: EditTextPreference? = findPreference(reminderIntervalKey)

        val pref = PreferenceManager.getDefaultSharedPreferences(context)

        switchNotification?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean) {
                val interval = pref.getString(reminderIntervalKey, "30")!!.toLong()

                val duration = Duration.ofMinutes(interval)

                scheduleReminder(duration)
            } else {
                WorkManager
                    .getInstance(requireContext())
                    .cancelAllWorkByTag(NOTIFICATION_WORKER_TAG)
            }
            true
        }

        reminderInterval?.setOnPreferenceChangeListener { _, newValue ->
            WorkManager
                .getInstance(requireContext())
                .cancelAllWorkByTag(NOTIFICATION_WORKER_TAG)

            val intervalMinutes = (newValue as String).toString().toLong()
            val duration = Duration.ofMinutes(intervalMinutes)
            scheduleReminder(duration)
            true
        }
    }

    private fun scheduleReminder(duration: Duration) {
        val notificationWorker = PeriodicWorkRequestBuilder<NotificationWorker>(duration)
            .addTag(NOTIFICATION_WORKER_TAG)
            .setInitialDelay(duration)
            .build()

        WorkManager
            .getInstance(requireContext())
            .enqueue(notificationWorker)
    }
}