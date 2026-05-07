package com.example.apnea

import android.content.Context
import android.media.MediaPlayer
import android.provider.Settings

class AlarmManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    fun triggerAlarm() {
        if (mediaPlayer != null) return  // alarm już gra

        mediaPlayer = MediaPlayer.create(
            context,
            Settings.System.DEFAULT_ALARM_ALERT_URI
        )

        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    fun stopAlarm() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}