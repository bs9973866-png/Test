package app.novaura.launcher.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.novaura.launcher.ui.cosmos.CosmosBackground
import app.novaura.launcher.ui.cosmos.CosmosQuality
import app.novaura.launcher.ui.theme.NovauraColors as C
import app.novaura.launcher.ui.theme.NovauraDims

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(vm: HomeViewModel) {
    val ui by vm.ui.collectAsState()
    val quality by vm.quality.collectAsState()
    var showSettings by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxSize()) {
        CosmosBackground(quality)
        Column(
            Modifier.fillMaxSize().systemBarsPadding().imePadding()
                .verticalScroll(rememberScrollState()).padding(NovauraDims.ScreenPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("NOVAURA", color = C.TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Black, letterSpacing = 6.sp)
            Text("MOBILE LAUNCHER", color = C.Teal, fontSize = 12.sp, letterSpacing = 4.sp)
            Spacer(Modifier.height(260.dp))
            Panel {
                Text(vm.profile.name, color = C.TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                Text("Minecraft ${vm.profile.mcVersion} · ${vm.profile.loader}", color = C.TextSecondary, fontSize = 14.sp)
            }
            Spacer(Modifier.height(20.dp))
            PlayButton(ui.state, onClick = vm::play)
            Spacer(Modifier.height(10.dp))
            StatusLine(ui)
            if (ui.state == GameState.PREPARING) {
                LinearProgressIndicator(progress = { ui.progress }, color = C.NeonPink, trackColor = C.Panel,
                    modifier = Modifier.fillMaxWidth(0.7f).padding(top = 8.dp))
                TextButton(onClick = vm::cancel) { Text("Отмена", color = C.LightPink) }
            }
            Spacer(Modifier.height(20.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Версии", "Моды", "Скины", "Инфо").forEach { NavChip(it) {} }
                NavChip("Настройки") { showSettings = true }
            }
            Spacer(Modifier.height(16.dp))
            Text("Графика: не выбрана · фон: ${quality.title}", color = C.TextSecondary, fontSize = 12.sp, textAlign = TextAlign.Center)
        }
    }
    if (showSettings) SettingsDialog(quality, vm::setQuality) { showSettings = false }
}

@Composable private fun Panel(content: @Composable ColumnScope.() -> Unit) = Column(
    Modifier.fillMaxWidth().background(C.Panel.copy(alpha = 0.85f), RoundedCornerShape(NovauraDims.PanelRadius)).padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally, content = content)

@Composable private fun PlayButton(state: GameState, onClick: () -> Unit) {
    val busy = state == GameState.PREPARING || state == GameState.RUNNING
    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        0.55f, 1f, infiniteRepeatable(tween(1600, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "p")
    val glow = if (busy) 0.25f else pulse
    Button(
        onClick = onClick, enabled = !busy,
        shape = RoundedCornerShape(32.dp),
        colors = ButtonDefaults.buttonColors(containerColor = C.Violet, disabledContainerColor = C.Panel),
        border = BorderStroke(2.dp, C.NeonPink.copy(alpha = glow)),
        modifier = Modifier.fillMaxWidth(0.8f).heightIn(min = 72.dp).drawBehind {
            drawCircle(Brush.radialGradient(listOf(C.NeonPink.copy(alpha = 0.35f * glow), Color.Transparent), Offset(size.width / 2, size.height / 2), size.width * 0.6f))
        },
    ) {
        Text(when (state) { GameState.PREPARING -> "ЗАГРУЗКА…"; GameState.RUNNING -> "ЗАПУЩЕНО"; else -> "ИГРАТЬ" },
            fontSize = 24.sp, fontWeight = FontWeight.Black, letterSpacing = 3.sp, color = C.TextPrimary)
    }
}

@Composable private fun StatusLine(ui: HomeUi) {
    val (text, color) = when (ui.state) {
        GameState.IDLE -> "Готов к запуску" to C.TextSecondary
        GameState.PREPARING -> ui.stage to C.Teal
        GameState.RUNNING -> ui.stage to C.Teal
        GameState.ERROR -> (ui.error ?: "Ошибка") to C.Error
    }
    Text(text, color = color, fontSize = 14.sp, textAlign = TextAlign.Center)
}

@Composable private fun NavChip(label: String, onClick: () -> Unit) = OutlinedButton(
    onClick = onClick, modifier = Modifier.heightIn(min = NovauraDims.MinTouch),
    border = BorderStroke(1.dp, C.Violet.copy(alpha = 0.7f)), shape = RoundedCornerShape(16.dp),
) { Text(label, color = C.TextPrimary) }

@Composable private fun SettingsDialog(current: CosmosQuality, onPick: (CosmosQuality) -> Unit, onClose: () -> Unit) =
    AlertDialog(
        onDismissRequest = onClose, containerColor = C.Panel,
        title = { Text("Качество космоса", color = C.TextPrimary) },
        text = { Column { CosmosQuality.entries.forEach { q ->
            Row(Modifier.fillMaxWidth().heightIn(min = NovauraDims.MinTouch), verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = q == current, onClick = { onPick(q) })
                Text(q.title, color = C.TextPrimary)
            } } } },
        confirmButton = { TextButton(onClick = onClose) { Text("Готово", color = C.Teal) } },
    )
