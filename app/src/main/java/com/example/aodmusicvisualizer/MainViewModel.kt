package com.example.aodmusicvisualizer

import android.media.audiofx.Visualizer
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModel:ViewModel(), Visualizer.OnDataCaptureListener {
    var something:Visualizer.MeasurementPeakRms = Visualizer.MeasurementPeakRms()
    var rms = MutableStateFlow<Double>(0.0)
    var peak = MutableStateFlow<Double>(0.0)
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

        rms.value = something.mRms / 100.0
        peak.value = something.mPeak / 100.0
    }


}

