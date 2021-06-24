package com.example.oxybite

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.oxybite.databinding.ActivityMainBinding
import com.example.oxybite.settings.SettingActivity
import com.example.oxybite.worker.DailyResetWorker
import java.time.Duration
import java.util.*

const val OXYBITE_SHARED_PREF = "shared"
const val RESET_WORKER_TAG = "resetTag"

class MainActivity : AppCompatActivity() {
    private var glassCount = 0
    private lateinit var sharedPref: SharedPreferences

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        sharedPref = this.getSharedPreferences(OXYBITE_SHARED_PREF, Context.MODE_PRIVATE)

        binding.glass.setOnClickListener {
            incrementCount()

        }
        binding.addBtn.setOnClickListener {
            incrementCount()
        }
        binding.subBtn.setOnClickListener {
            decrementCount()
        }

        scheduleResetJob()
    }

    override fun onResume() {
        super.onResume()
        glassCount = sharedPref.getInt(getString(R.string.glass_count), 0)
        binding.incNum.text = glassCount.toString()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.appSetting -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun incrementCount() {
        glassCount++
        binding.incNum.text = glassCount.toString()
        with(sharedPref.edit()) {
            putInt(getString(R.string.glass_count), glassCount)
            apply()
        }
    }

    private fun decrementCount() {
        if (glassCount == 0) {
            Toast.makeText(
                this, "Glass count cannot be less than 0", Toast.LENGTH_SHORT
            ).show()
            return
        }
        glassCount--
        binding.incNum.text = glassCount.toString()
        with(sharedPref.edit()) {
            putInt(getString(R.string.glass_count), glassCount)
            apply()
        }

    }

    private fun scheduleResetJob() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)

        val timeDifference = calendar.timeInMillis - System.currentTimeMillis()
        val backOff = Duration.ofMillis(timeDifference)
        val duration = Duration.ofHours(24)

        val resetWorker = PeriodicWorkRequestBuilder<DailyResetWorker>(duration)
            .addTag(RESET_WORKER_TAG)
            .setInitialDelay(backOff)
            .build()

        WorkManager
            .getInstance(this)
            .enqueueUniquePeriodicWork(
                "Periodic Worker",
                ExistingPeriodicWorkPolicy.KEEP,
                resetWorker
            )
    }
}
