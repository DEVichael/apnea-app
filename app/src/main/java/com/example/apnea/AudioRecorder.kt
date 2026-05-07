package com.example.apnea

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder

class AudioRecorder(private val context: Context) {

    private val sampleRate = 16000  // 16 kHz — idealne pod MFCC i TFLite
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT

    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        channelConfig,
        audioFormat
    )

    fun record10Seconds(): ShortArray {
        val recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        val totalSamples = sampleRate * 10   // 10 sekund
        val audioBuffer = ShortArray(totalSamples)

        recorder.startRecording()

        var offset = 0
        while (offset < totalSamples) {
            val read = recorder.read(
                audioBuffer,
                offset,
                totalSamples - offset
            )
            if (read > 0) offset += read
        }

        recorder.stop()
        recorder.release()

        return audioBuffer
    }
}