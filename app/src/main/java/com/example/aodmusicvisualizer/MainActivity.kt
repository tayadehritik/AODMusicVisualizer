package com.example.aodmusicvisualizer

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.aodmusicvisualizer.ui.theme.AODMusicVisualizerTheme
import android.Manifest

import android.animation.ValueAnimator
import android.graphics.RuntimeShader
import android.media.MediaPlayer
import android.media.SoundPool
import android.view.animation.LinearInterpolator
import androidx.compose.foundation.*

import android.media.audiofx.Visualizer
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Switch
import androidx.compose.material3.Card
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.Typography
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject
import kotlin.concurrent.timer
import kotlin.concurrent.timerTask

// use it to animate the time uniform



var modifyAudioSettingsPermissionGranted = false
var recordAudioPermissionGranted = false

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    var visualizer = Visualizer(0)
    private val clientId = "74aaba15df23432ea5795d9668f8d058"
    private val redirectUri = "http://localhost:3000"
    private var spotifyAppRemote: SpotifyAppRemote? = null
    @Inject
    lateinit var mediaPlayer: MediaPlayer
    var tempo:Double = 100.0
    private var isRunning:Boolean = false
    private lateinit var handler: Handler

    private val metronomeRunnable = object : Runnable{
        override fun run() {
            if(isRunning) {
                mediaPlayer.start()
                handler.postDelayed(this, (60000/tempo).toLong())
            }

        }
    }
    private fun connected() {
        spotifyAppRemote?.let {
            // Play a playlist
            //val playlistURI = "spotify:playlist:37i9dQZF1DX2sUQwD7tbmL"
            //it.playerApi.play(playlistURI)
            // Subscribe to PlayerState
            it.playerApi.playerState.setResultCallback {
                println("${it.playbackPosition}")
            }
            it.playerApi.subscribeToPlayerState().setEventCallback {
                val track: Track = it.track
                if(it.isPaused) {
                    /*isRunning = false
                    mediaPlayer.stop()
                    mediaPlayer.prepare()*/

                }
                else {
                    println(it.track.uri)
                    /*isRunning = true
                    handler.post(metronomeRunnable)*/
                }
                Log.d("MainActivity", track.name + " by " + track.artist.name)
                Log.d("Playback Position", "Playback position ${it.playbackPosition}")
            }
        }

    }

    override fun onStart() {
        super.onStart()

        handler = Handler(Looper.getMainLooper())
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        var soundPool = SoundPool.Builder().build()
        var id = soundPool.load(this,R.raw.mode_2_first,1)

        Timer().scheduleAtFixedRate(timerTask {
                soundPool.play(id,1.0f,1.0f,1,0,1.0f)
        },0,((60/tempo)*1000).toLong())

        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
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

    override fun onStop() {
        super.onStop()
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }

    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var listener = ViewModelProvider(this)[MainViewModel::class.java]
        lifecycleScope.launch(Dispatchers.IO) {

            visualizer.setDataCaptureListener(listener, Visualizer.getMaxCaptureRate(),false,true)
            visualizer.setMeasurementMode(Visualizer.MEASUREMENT_MODE_PEAK_RMS)
            visualizer.setEnabled(true)
        }

        setContent {

            AODMusicVisualizerTheme {
                //TopAppBar()
                var rms = listener.rms.collectAsState()
                var peak = listener.peak.collectAsState()
                var audioAnalysis = listener.audioAnalysis.collectAsState()

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val barWidth = canvasWidth / audioAnalysis.value.size
                    var xVal = 0f
                    for(bar in audioAnalysis.value) {
                        drawRect(
                            topLeft = Offset(xVal,canvasHeight/2f),
                            color = Color.Red,
                            size = Size(barWidth,1-(bar.second.first +rms.value.toFloat()))
                        )
                        xVal += barWidth
                    }

                }
            }

        }
    }
}




@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TopAppBar() {
    var AODServiceActive by remember { mutableStateOf(true) }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {

                    Text(
                        "AOD Music Visualizer",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        },
        content = {  innerPadding ->
            Divider(Modifier.padding(innerPadding))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = innerPadding.calculateTopPadding() + 10.dp,
                        start = 10.dp,
                        end = 10.dp
                    )
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Permissions Granted",
                    style = MaterialTheme.typography.labelMedium
                )
                getRecordAudioPermission()
                getModifyAudioSettingsPermission()
                Divider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "AOD Service"
                    )
                    Spacer(
                        Modifier
                            .weight(1f)
                            .fillMaxHeight())
                    Switch(
                        modifier = Modifier.semantics { contentDescription = "Demo" },
                        checked = AODServiceActive,
                        onCheckedChange =  { AODServiceActive = it })
                }
                Divider()
                Text(
                    text = "Visualizers"
                )
                Column(modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp))
                {
                    for(i in 1..1)
                    {
                        Row(modifier = Modifier
                            .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly)
                        {
                            visualizerCard()
                        }
                    }

                }

            }

        }
    )
}




@Preview
@Composable
fun visualizerCard(name:String="test" )
{
    val speed: Float = 0.01f
    val time = remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        do {
            withFrameMillis {
                time.value = time.value + speed
            }
        } while (true)
    }
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)


    ) {
        Text(
            text = "Bar Vizualizer",
            modifier = Modifier
                .padding(16.dp),
            textAlign = TextAlign.Center,
        )
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

        }

    }

}



@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun getRecordAudioPermission() {
    val recordAudioPermissionState = rememberPermissionState(permission =
    Manifest.permission.RECORD_AUDIO
    )
    if (recordAudioPermissionState.status.isGranted) {
        recordAudioPermissionGranted = true
    } else {
        Column {
            val textToShow = if (recordAudioPermissionState.status.shouldShowRationale) {
                // If the user has denied the permission but the rationale can be shown,
                // then gently explain why the app requires this permission
                "Record Audio is important for this app. Please grant the permission."
            } else {
                // If it's the first time the user lands on this feature, or the user
                // doesn't want to be asked again for this permission, explain that the
                // permission is required
                "Record Audio permission required for this feature to be available. " +
                        "Please grant the permission"
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Record Audio"
                )
                Spacer(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight())
                Button(onClick = { recordAudioPermissionState.launchPermissionRequest()  }) {
                    Text("Grant")
                }


            }


        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun getModifyAudioSettingsPermission() {
    val modifyAudioSettingsPermissionState = rememberPermissionState(permission =
    Manifest.permission.MODIFY_AUDIO_SETTINGS
    )
    if (modifyAudioSettingsPermissionState.status.isGranted) {
        modifyAudioSettingsPermissionGranted = true;
    } else {
        Column {
            val textToShow = if (modifyAudioSettingsPermissionState.status.shouldShowRationale) {
                // If the user has denied the permission but the rationale can be shown,
                // then gently explain why the app requires this permission
                "Modify Audio Settings is important for this app. Please grant the permission."
            } else {
                // If it's the first time the user lands on this feature, or the user
                // doesn't want to be asked again for this permission, explain that the
                // permission is required
                "Modify Audio Settings permission required for this feature to be available. " +
                        "Please grant the permission"
            }


            Row(
                modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = textToShow
                )
                Spacer(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight())
                Button(onClick = { modifyAudioSettingsPermissionState.launchPermissionRequest() }) {
                    Text("Grant")
                }

            }

        }
    }
}







