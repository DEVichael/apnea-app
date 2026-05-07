package com.example.apnea

import kotlin.math.log10
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.cos

class FeatureExtractor {

    private val sampleRate = 16000
    private val frameSize = 400          // 25 ms
    private val frameStep = 160          // 10 ms
    private val numFilters = 26
    private val numCoefficients = 13

    fun extractMFCC(audio: ShortArray): Array<FloatArray> {
        val floatAudio = audio.map { it.toFloat() }.toFloatArray()

        val frames = framing(floatAudio)
        val powerSpectra = frames.map { powerSpectrum(it) }
        val melEnergies = powerSpectra.map { melFilterBank(it) }
        val logMel = melEnergies.map { row -> row.map { log10(max(it, 1e-10f)) }.toFloatArray() }
        val mfcc = logMel.map { dct(it) }

        return mfcc.toTypedArray()
    }

    private fun framing(signal: FloatArray): List<FloatArray> {
        val frames = mutableListOf<FloatArray>()
        var start = 0

        while (start + frameSize <= signal.size) {
            val frame = signal.copyOfRange(start, start + frameSize)
            frames.add(applyHamming(frame))
            start += frameStep
        }

        return frames
    }

    private fun applyHamming(frame: FloatArray): FloatArray {
        val N = frame.size
        return FloatArray(N) { i ->
            frame[i] * (0.54f - 0.46f * cos(2 * Math.PI * i / (N - 1))).toFloat()
        }
    }

    private fun powerSpectrum(frame: FloatArray): FloatArray {
        val N = frame.size
        val spectrum = FloatArray(N / 2)

        for (k in 0 until N / 2) {
            var real = 0f
            var imag = 0f

            for (n in frame.indices) {
                val angle = (2 * Math.PI * k * n / N).toFloat()
                real += frame[n] * cos(angle)
                imag -= frame[n] * kotlin.math.sin(angle)
            }

            spectrum[k] = real * real + imag * imag
        }

        return spectrum
    }

    private fun melFilterBank(powerSpectrum: FloatArray): FloatArray {
        val melEnergies = FloatArray(numFilters)

        for (i in 0 until numFilters) {
            var sum = 0f
            for (j in powerSpectrum.indices) {
                sum += powerSpectrum[j] * (1f / (j + 1))  // uproszczony filtr
            }
            melEnergies[i] = sum
        }

        return melEnergies
    }

    private fun dct(mel: FloatArray): FloatArray {
        val result = FloatArray(numCoefficients)

        for (k in 0 until numCoefficients) {
            var sum = 0f
            for (n in mel.indices) {
                sum += mel[n] * cos(Math.PI * k * (2 * n + 1) / (2 * mel.size)).toFloat()
            }
            result[k] = sum
        }

        return result
    }
}