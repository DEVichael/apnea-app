package com.example.apnea

class Normalizer {

    fun normalize(mfcc: Array<FloatArray>): Array<FloatArray> {
        val flat = mfcc.flatMap { it.toList() }
        val mean = flat.average().toFloat()
        val std = flat.map { (it - mean) * (it - mean) }.average().toFloat().let {
            if (it == 0f) 1f else kotlin.math.sqrt(it)
        }

        return mfcc.map { row ->
            row.map { (it - mean) / std }.toFloatArray()
        }.toTypedArray()
    }
}