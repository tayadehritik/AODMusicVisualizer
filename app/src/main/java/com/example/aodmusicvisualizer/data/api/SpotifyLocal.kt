package com.example.aodmusicvisualizer.data.api

import android.app.Application
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.example.aodmusicvisualizer.Metronome
import com.example.example.TrackAnalysis
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.timerTask


class SpotifyLocal(
    var app:Application,
    var spotifyAPI: SpotifyAPI,
    var metronome: Metronome,
    var spotifyAuthAPI: SpotifyAuthAPI
) {

    private val clientId = "74aaba15df23432ea5795d9668f8d058"
    private val redirectUri = "http://localhost:3000"
    private var spotifyAppRemote: SpotifyAppRemote? = null



    lateinit var trackAnalysis: TrackAnalysis
    var currentTempo:Double = 1.0
    private fun connected() {
        spotifyAppRemote?.let {
            Timer().scheduleAtFixedRate(timerTask {
                it.playerApi.playerState.setResultCallback { playerState ->
                    if(this@SpotifyLocal::trackAnalysis.isInitialized) {
                        var idealSection = trackAnalysis!!.sections.findLast {
                            (it.start!!) *1000 <= playerState.playbackPosition
                        }
                        //println(trackAnalysis.sections)
                        if(idealSection!!.tempo != currentTempo) {
                            println("${idealSection!!.start!!*1000} idealSelection start ${playerState.playbackPosition} playbackPosition ")
                            metronome.updateTempo(idealSection!!.tempo!!, idealSection!!.timeSignature!!, ((idealSection!!.start!!*1000) - (playerState.playbackPosition)).toLong())
                            currentTempo = idealSection!!.tempo!!
                        }
                        //println("$idealSection!! ${playerState.playbackPosition}")
                    }
                }
            },0,1)

            it.playerApi.subscribeToPlayerState().setEventCallback {playerState ->
                val track: Track = playerState.track
                if(playerState.isPaused) {
                    metronome.stop()
                }
                else {

                    CoroutineScope(Dispatchers.IO).launch {
                        //println(spotifyAPI.getTrackAnalysis(it.track.uri.split(":")[2]))
                        trackAnalysis = spotifyAPI.getTrackAnalysis(playerState.track.uri.split(":")[2],
                            "Bearer "+spotifyAuthAPI.getToken().body()!!.access_token).body()!!

                    }

                }
                Log.d("MainActivity", track.name + " by " + track.artist.name)
                Log.d("Playback Position", "Playback position ${playerState.playbackPosition}")
            }
        }

    }

    init {
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()
        SpotifyAppRemote.connect(app, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                Log.d("MainActivity", "Connected! Yay!")
                // Now you can start interacting with App Remote
                //isRunning = true
                //handler.post(metronomeRunnable)
                connected()
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("MainActivity", throwable.message, throwable)
                // Something went wrong when attempting to connect! Handle errors here
            }
        })
    }
}