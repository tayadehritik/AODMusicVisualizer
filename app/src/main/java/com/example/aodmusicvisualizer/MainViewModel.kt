package com.example.aodmusicvisualizer

import android.media.audiofx.Visualizer
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.atan2
import kotlin.math.hypot


class MainViewModel:ViewModel(), Visualizer.OnDataCaptureListener {
    var something:Visualizer.MeasurementPeakRms = Visualizer.MeasurementPeakRms()
    var rms = MutableStateFlow<Double>(0.0)
    var peak = MutableStateFlow<Double>(0.0)
    var _audioAnalysis = MutableStateFlow<List<Pair<Double,Pair<Float,Float>>>>(listOf(Pair(0.0,Pair(0f,0f))))
    val audioAnalysis = _audioAnalysis.asStateFlow()
    fun startVisualizer() {
        println("here")

    }

    override fun onWaveFormDataCapture(
        visualizer: Visualizer?,
        waveform: ByteArray?,
        samplingRate: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun onFftDataCapture(visualizer: Visualizer?, fft: ByteArray?, samplingRate: Int) {
        //println(fft!!.toList().toString())
        visualizer!!.getMeasurementPeakRms(something)
        val n: Int = fft!!.size
        val magnitudes = FloatArray(n / 2 + 1)
        val frequencies = FloatArray(n / 2 + 1)
        val phases = FloatArray(n / 2 + 1)
        magnitudes[0] = Math.abs(fft!![0].toFloat()) // DC
        magnitudes[n / 2] = Math.abs(fft!![1].toFloat())// Nyquist
        phases[0] = 0.also { phases[n / 2] = it.toFloat() }.toFloat()
        for (k in 1 until n / 2) {
            val i = k * 2
            frequencies[k] = (k * samplingRate.toFloat()) / n
            magnitudes[k] = hypot(fft!![i].toDouble(), fft!![i + 1].toDouble()).toFloat()
            phases[k] = atan2(fft!![i + 1].toDouble(), fft!![i].toDouble()).toFloat()
        }

        val zipped = magnitudes.zip(phases)
        _audioAnalysis.value = frequencies.zip(zipped) { frequency, other ->
            Pair(frequency/1000.0,other)
        }
        //println(frequencies.map { it/1000.0 })
        rms.value = magnitudes.zip(phases)[1].first.toDouble()
        //rms.value = something.mRms / 100.0
        peak.value = something.mPeak / 100.0
    }


}

