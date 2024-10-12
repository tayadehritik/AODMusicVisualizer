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
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.RuntimeShader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Switch
import androidx.compose.material3.Card
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Typography
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.VectorProperty
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import coil3.asDrawable
import coil3.compose.AsyncImagePainter
import coil3.compose.ConstraintsSizeResolver
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.size.Scale
import coil3.toBitmap
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.Placeholder
import com.bumptech.glide.integration.compose.placeholder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.aodmusicvisualizer.data.api.SpotifyAPI
import com.example.aodmusicvisualizer.data.api.SpotifyAuthAPI
import com.example.aodmusicvisualizer.data.api.SpotifyLocal
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
import java.nio.file.WatchEvent
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

    @Inject
    lateinit var metronome: Metronome
    @Inject
    lateinit var spotifyLocal:SpotifyLocal

    override fun onStart() {
        super.onStart()


    }

    override fun onStop() {
        super.onStop()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO("Implement Time Signature with Visuals not audio needed")
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
                var beat = metronome.beat.collectAsState()
                var borderColor = listener.borderColor.collectAsState()
                var albumArtURL = spotifyLocal.albumArtURL.collectAsState()
                /*Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val barWidth = canvasWidth / audioAnalysis.value.size
                    var xVal = 0f
                    *//*for(bar in audioAnalysis.value) {
                        drawRect(
                            topLeft = Offset(xVal,canvasHeight/2f),
                            color = Color.Red,
                            size = Size(barWidth,1-(bar.second.first +rms.value.toFloat()))
                        )
                        xVal += barWidth
                    }*//*

                    scale(beat.value) {
                        drawCircle(
                            color = Color.Blue,
                            radius = size.minDimension / 2.0f,
                            alpha = 0.5f
                        )
                    }


                }*/
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Absolute.Center,
                    verticalAlignment = Alignment.CenterVertically) {

                    CoilVisualizerCircle(
                        beat.value,
                        borderColor.value,
                        albumArtURL = albumArtURL.value,
                        updateBorderColor = {
                            listener.updateBorderColor(it)
                        }
                    )
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



@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun visualizerCircle(
    scale:Float = 1.0f,
    borderColor:Int = -14675952,
    albumArtURL:String = "",
    updateBorderColor:(Int) -> Unit = {}
) {
    val shadowImage = ImageBitmap.imageResource(id = R.drawable.shadow)
    var albumImage = ImageBitmap.imageResource(id = R.drawable.album_art)

    val borderWidth = 15.dp
    var dominantColor : Int = 0
    var context = LocalContext.current
    println("albumArtURL $albumArtURL")


    GlideImage(
        model = albumArtURL,
        contentDescription = "",
        contentScale = ContentScale.Crop,
        loading = placeholder(ColorPainter(Color.Blue)),
        modifier = Modifier
            .size(215.dp)
            .scale(scale)
            .border(
                BorderStroke(borderWidth, Color(borderColor)),
                CircleShape
            )
            .shadow(5.dp, CircleShape)
            .padding(borderWidth)
            .drawWithContent {
                drawImage(
                    albumImage,
                )
                drawImage(
                    shadowImage,
                    topLeft = Offset(2f, 0f)
                )

            }
            .clip(CircleShape)
    ) {
        it.addListener(object :RequestListener<Drawable> {
            override fun onLoadFailed(
                p0: GlideException?,
                p1: Any?,
                p2: Target<Drawable>,
                p3: Boolean
            ): Boolean {

                return false
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                val drawable  = resource as BitmapDrawable
                albumImage = drawable.bitmap.asImageBitmap()
                val bitmap  = drawable.bitmap
                Palette.Builder(bitmap).generate{
                    it?.let { palette ->
                        updateBorderColor(palette.getDominantColor(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        ))

                    }
                }

                return true
            }

        })
    }

    /*Canvas(modifier = Modifier
        .fillMaxSize()
        .shadow(5.dp, CircleShape, true)) {
        scale(1f) {
            drawCircle(
                color = Color.Blue,
                radius = size.minDimension / 2.0f,
                alpha = 0.5f
            )
        }
    }*/

}

@Composable
@Preview
fun CoilVisualizerCircle(
    scale:Float = 1.0f,
    borderColor:Int = -14675952,
    albumArtURL:String = "",
    updateBorderColor:(Int) -> Unit = {}
) {
    println(albumArtURL)
    var context = LocalContext.current
    val shadowImage = ImageBitmap.imageResource(id = R.drawable.shadow)
    val sizeResolver = remember{ ConstraintsSizeResolver()}
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .data(albumArtURL)
            .size(sizeResolver)
            .allowHardware(false)
            .build(),
        onSuccess = {resultState ->
            Palette.Builder(resultState.result.image.toBitmap()).generate {
                it?.let { palette ->
                    updateBorderColor(palette.getDominantColor(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    ))
                }

            }
        }
    )

    Image(
        painter = painter,
        contentDescription = "",
        modifier = Modifier
            .size(250.dp)
            .then(sizeResolver)
            .scale(scale)
            .clip(CircleShape)
            .border(
                BorderStroke(15.dp, Color(borderColor)),
                CircleShape
            )
            .drawWithContent {
                // Draw the original content (image and border)
                drawContent()
                drawImage(
                    shadowImage,
                    dstOffset = IntOffset(0,0),
                    dstSize = IntSize(size.width.toInt(),size.height.toInt()),
                    blendMode = BlendMode.SrcOver
                )
            }

    )
}








