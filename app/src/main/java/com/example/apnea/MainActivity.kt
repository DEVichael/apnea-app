package com.example.apnea

import android.util.Log
import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.apnea.AudioRecorder
import com.example.apnea.FeatureExtractor
import com.example.apnea.Normalizer
import com.example.apnea.ApneaDetector
import com.example.apnea.AlarmManager

class MainActivity : ComponentActivity() {

    private lateinit var recorder: AudioRecorder
    private lateinit var extractor: FeatureExtractor
    private lateinit var normalizer: Normalizer
    private lateinit var detector: ApneaDetector
    private lateinit var alarm: AlarmManager

    private val handler = Handler(Looper.getMainLooper())

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) startMonitoring()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recorder = AudioRecorder(this)
        extractor = FeatureExtractor()
        normalizer = Normalizer()
        detector = ApneaDetector(this)
        alarm = AlarmManager(this)

        checkMicrophonePermission()
    }

    private fun checkMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startMonitoring()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun startMonitoring() {
        handler.post(object : Runnable {
            override fun run() {
                monitorApnea()
                handler.postDelayed(this, 10_000) // co 10 sekund
            }
        })
    }

    private fun monitorApnea() {
        val audioData = recorder.record10Seconds()
        val mfcc = extractor.extractMFCC(audioData)
        val normalized = normalizer.normalize(mfcc)
        val isApnea = detector.detect(mfcc = normalized)

        Log.d("APNEA", "Nagrywanie OK")
        Log.d("APNEA", "MFCC: ${mfcc.size} ramek")
        Log.d("APNEA", "Wynik modelu: $isApnea")

        if (isApnea) {
            alarm.triggerAlarm()
        }
    }
}
