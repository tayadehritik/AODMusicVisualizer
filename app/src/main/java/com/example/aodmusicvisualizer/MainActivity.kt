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
import android.view.animation.LinearInterpolator
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.text.style.TextAlign
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

private const val DURATION = 4000f
private const val COLOR_SHADER_SRC = """
   uniform float2 iResolution;
   uniform float iTime;
   
   float drawRect(vec2 coords)
   {
        vec2 bl = step(vec2(0.1),coords);
        float pct = bl.x * bl.y;
        
        vec2 tr = step(vec2(0.1),1.0-coords);
        pct = pct * tr.x * tr.y;
        
        vec2 bl2 = step(vec2(0.2), coords);
        vec2 tr2 = step(vec2(0.2),1.0-coords);
        float pct2 = bl2.x * bl2.y * tr2.x * tr2.y;
        pct = pct - pct2;
        return pct;
   }
   
   float drawBars(vec2 coords)
   {
        
        
        float pct = step(0.2,mod((coords.x*10),0.8));
        float valy = 1.0 - (step(abs(sin(coords.x*2.0+iTime))-0.1 ,coords.y));
        pct *= valy;
        
        return pct;
   }
   
   half4 main(float2 fragCoord) 
    {
       float2 st = fragCoord/iResolution.xy;
       st.y = 1.0-st.y;
       vec3 color = vec3(0.0);

       color = vec3(drawBars(st));
       
       return half4(color,1.0);
    }
"""
// created as top level constants
val colorShader = RuntimeShader(COLOR_SHADER_SRC)
val shaderBrush = ShaderBrush(colorShader)
// declare the ValueAnimator
private val shaderAnimator = ValueAnimator.ofFloat(0f, DURATION)

// use it to animate the time uniform



var modifyAudioSettingsPermissionGranted = false
var recordAudioPermissionGranted = false


class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {

            AODMusicVisualizerTheme() {
                TopAppBar()
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
        modifier = Modifier.fillMaxWidth().height(400.dp)


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
            colorShader.setFloatUniform("iResolution",
                size.width, size.height)
            colorShader.setFloatUniform(
                "iTime",
                time.value
            )

            drawRect(brush = shaderBrush)
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







