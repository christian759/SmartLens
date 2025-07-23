package com.vision.smartlens


import android.Manifest
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.vision.smartlens.camera.CameraPreview
import java.util.*

class MainActivity : ComponentActivity() {
    private lateinit var tts: TextToSpeech

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.US
            }
        }
        enableEdgeToEdge()
        setContent {
            CameraScreen()
        }
    }

}
@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> permissionGranted = granted }
    )

    LaunchedEffect(Unit) {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    if (permissionGranted) {
        CameraPreview(modifier)
    } else {
        Log.d("SmartLens", "Camera permission not granted yet.")
    }
}


