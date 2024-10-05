package com.example.aodmusicvisualizer

import android.app.Application
import android.media.MediaPlayer
import android.media.SoundPool
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Timer
import kotlin.concurrent.timerTask

class Metronome(
    app:Application
) {

    var tempo:Double = 129.0
    var timeSig:Double = 1.0
    var delay: Long = 0

    var _beat = MutableStateFlow(1f)
    var beat = _beat.asStateFlow()

    var timer = Timer()
    var soundPool = SoundPool.Builder().build()
    var id = soundPool.load(app,R.raw.mode_2_first,1)
    fun start() {
        stop()

        println("$delay delay")
        timer.scheduleAtFixedRate(timerTask {
            //soundPool.play(id,1.0f,1.0f,1,0,1.0f)
            /*println("$tempo tempo")
            println("$timeSig timeSig")*/
            _beat.value = 1f
        },0,((60/tempo)*1000).toLong())
    }

    fun stop() {
        timer.cancel()
        timer.purge()
        timer = Timer()
    }

    fun updateTempo(nTempo:Double, nTimeSig:Double, nDelay: Long) {
        tempo = nTempo
        timeSig = nTimeSig
        if(delay > 0) delay = nDelay
        start()
    }

    fun decreaseBeatValue() {
        _beat.value = _beat.value - 0.00025f
    }
}