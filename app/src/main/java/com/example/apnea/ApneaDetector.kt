package com.example.apnea

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ApneaDetector(private val context: Context) {

    private val interpreter: Interpreter

    init {
        val model = loadModel("apnea_model.tflite")
        interpreter = Interpreter(model)
    }

    private fun loadModel(filename: String): ByteBuffer {
        val bytes = context.assets.open(filename).readBytes()
        return ByteBuffer.allocateDirect(bytes.size).apply {
            order(ByteOrder.nativeOrder())
            put(bytes)
            rewind()
        }
    }

    fun detect(mfcc: Array<FloatArray>): Boolean {
        val input = arrayOf(mfcc)
        val output = Array(1) { FloatArray(1) }

        interpreter.run(input, output)

        val probability = output[0][0]
        return probability > 0.5f
    }
}