package com.example.beecanteen.data.repository

import kotlin.jvm.java
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.beecanteen.receiver.PollNotificationReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

// Top-level property ensures DataStore acts as a true Singleton
val Context.dataStore by preferencesDataStore(name = "custom_alarms")

@Singleton
class UserAlarmManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val ALARMS_KEY = stringSetPreferencesKey("ALARM_SET")

    // Expose DataStore as a real-time Flow for your ViewModel
    val savedAlarmsFlow: Flow<List<String>> = context.dataStore.data.map { preferences ->
        preferences[ALARMS_KEY]?.toList()?.sorted() ?: emptyList()
    }

    suspend fun scheduleAlarm(hour: Int, minute: Int) {
        val timeString = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
        val requestCode = generateRequestCode(hour, minute)

        // 1. Save to modern DataStore safely
        context.dataStore.edit { preferences ->
            val currentAlarms = preferences[ALARMS_KEY] ?: emptySet()
            preferences[ALARMS_KEY] = currentAlarms + timeString
        }

        // 2. Schedule with AlarmManager
        val intent = Intent(context, PollNotificationReceiver::class.java).apply {
            putExtra("TITLE", "Time to Vote!")
            putExtra("MESSAGE", "check the canteen polls.")
            putExtra("NOTIF_ID", requestCode)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        // If time already passed today, set for tomorrow
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    suspend fun cancelAlarm(timeString: String) {
        val parts = timeString.split(":")
        if (parts.size != 2) return
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()
        val requestCode = generateRequestCode(hour, minute)

        // 1. Remove from DataStore safely
        context.dataStore.edit { preferences ->
            val currentAlarms = preferences[ALARMS_KEY] ?: emptySet()
            preferences[ALARMS_KEY] = currentAlarms - timeString
        }

        // 2. Cancel the AlarmManager
        val intent = Intent(context, PollNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun generateRequestCode(hour: Int, minute: Int): Int {
        return (hour * 100) + minute
    }
}