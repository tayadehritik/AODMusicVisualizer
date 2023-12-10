package com.example.aodmusicvisualizer

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.aodmusicvisualizer.ui.theme.AODMusicVisualizerTheme
import android.Manifest
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

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
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)


@Preview(showBackground = true)
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
                    for(i in 0..1)
                    {
                        Row(modifier = Modifier
                            .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly)
                        {
                            OutlinedCard(
                                Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .height(100.dp))
                            {
                                // Card content
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            OutlinedCard(
                                Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .height(100.dp))
                            {
                                // Card content


                            }

                        }
                    }

                }

            }

        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AODMusicVisualizerTheme {
        TopAppBar()
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







