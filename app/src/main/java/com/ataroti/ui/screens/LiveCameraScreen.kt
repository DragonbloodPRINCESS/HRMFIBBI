package com.ataroti.ui.screens

import android.annotation.SuppressLint
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.ataroti.data.db.CardEntity
import com.ataroti.data.db.PlacedCardEntity
import com.ataroti.data.model.Orientation
import com.ataroti.ui.components.DeckPickerBottomSheet
import com.ataroti.ui.viewmodel.LiveOracleViewModel
import kotlin.math.roundToInt

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveCameraScreen(viewModel: LiveOracleViewModel, onOpenSessions: () -> Unit, onOpenSandbox: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val placed by viewModel.placed.collectAsState()
    val cards by viewModel.cards.collectAsState()
    val summary by viewModel.summary.collectAsState()

    var showPicker by remember { mutableStateOf(false) }
    var pendingCard by remember { mutableStateOf<CardEntity?>(null) }
    var orientation by remember { mutableStateOf(Orientation.UPRIGHT) }
    var clarifierFor by remember { mutableLongStateOf(-1L) }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(factory = {
            PreviewView(it).apply {
                val provider = ProcessCameraProvider.getInstance(it).get()
                val preview = Preview.Builder().build().also { p -> p.setSurfaceProvider(surfaceProvider) }
                provider.unbindAll()
                provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview)
            }
        }, modifier = Modifier.fillMaxSize())

        Box(Modifier.fillMaxSize().pointerInput(pendingCard, orientation) {
            detectTapGestures(onTap = { off ->
                pendingCard?.let {
                    val parent = clarifierFor.takeIf { id -> id > 0 }
                    viewModel.addCard(it, orientation, off.x / size.width, off.y / size.height, parent)
                    pendingCard = null
                    clarifierFor = -1L
                }
            })
        })

        placed.forEach { item ->
            OverlayCard(item = item,
                onUpdate = viewModel::updateCard,
                onClarifier = {
                    clarifierFor = item.id
                    showPicker = true
                })
        }

        Column(Modifier.align(Alignment.BottomCenter).fillMaxWidth().background(Color(0xAA111111)).padding(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { showPicker = true }) { Text("Add Card") }
                Button(onClick = onOpenSandbox) { Text("Sandbox") }
                Button(onClick = onOpenSessions) { Text("Sessions") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { orientation = Orientation.UPRIGHT }) { Text("Upright") }
                Button(onClick = { orientation = Orientation.REVERSED }) { Text("Reversed") }
            }
            summary.take(3).forEach { Text(it, color = Color(0xFFE8E0CF)) }
        }

        if (showPicker) {
            DeckPickerBottomSheet(cards = cards, onQuery = viewModel::setQuery, onSelect = {
                pendingCard = it
                showPicker = false
            }, onDismiss = { showPicker = false })
        }
    }
}

@Composable
private fun OverlayCard(item: PlacedCardEntity, onUpdate: (PlacedCardEntity) -> Unit, onClarifier: () -> Unit) {
    var x by remember(item.id) { mutableFloatStateOf(item.xNorm) }
    var y by remember(item.id) { mutableFloatStateOf(item.yNorm) }
    var scale by remember(item.id) { mutableFloatStateOf(item.scale) }
    var rotation by remember(item.id) { mutableFloatStateOf(item.rotationDeg) }
    val transform = rememberTransformableState { zoomChange, panChange, rotationChange ->
        x += panChange.x / 1080f
        y += panChange.y / 1920f
        scale = (scale * zoomChange).coerceIn(0.6f, 2.5f)
        rotation += rotationChange
    }

    Surface(
        modifier = Modifier
            .offset { IntOffset((x * 1080).roundToInt(), (y * 1920).roundToInt()) }
            .transformable(transform)
            .pointerInput(item.id) {
                detectTapGestures(onLongPress = { onClarifier() }, onPress = {
                    tryAwaitRelease()
                    onUpdate(item.copy(xNorm = x, yNorm = y, scale = scale, rotationDeg = rotation))
                })
            }
            .size(118.dp, 164.dp)
            .border(1.dp, Color(0x88E8E0CF), RoundedCornerShape(8.dp)),
        color = Color(0x66181818)
    ) {
        Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Text(item.title, color = Color(0xFFE8E0CF))
            Text(item.orientation.name, color = Color(0xFFCFC7B7))
        }
    }
}
