package app.novaura.launcher.ui.cosmos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import app.novaura.launcher.ui.theme.NovauraColors
import kotlin.math.*
import kotlin.random.Random

private class Stars(n: Int) {
    val x = FloatArray(n); val y = FloatArray(n); val s = FloatArray(n); val ph = FloatArray(n)
    init { val r = Random(7); for (i in 0 until n) { x[i] = r.nextFloat(); y[i] = r.nextFloat(); s[i] = 0.6f + r.nextFloat() * 1.8f; ph[i] = r.nextFloat() * 6.28f } }
}
private class Disk(n: Int) {
    val a = FloatArray(n); val rad = FloatArray(n); val sp = FloatArray(n); val c = IntArray(n)
    init { val r = Random(11); for (i in 0 until n) { a[i] = r.nextFloat() * 6.28f; rad[i] = 1.25f + r.nextFloat() * 1.9f; sp[i] = 0.9f + r.nextFloat() * 0.8f; c[i] = r.nextInt(3) } }
}
private val diskColors = arrayOf(NovauraColors.NeonPink, NovauraColors.Violet, NovauraColors.Teal)

/** Космос с чёрной дырой на Canvas; анимация приостанавливается в фоне. */
@Composable
fun CosmosBackground(quality: CosmosQuality, modifier: Modifier = Modifier) {
    if (quality == CosmosQuality.OFF) {
        Box(modifier.background(NovauraColors.Background)); return
    }
    val stars = remember(quality) { Stars(quality.stars) }
    val disk = remember(quality) { Disk(quality.diskParticles) }
    var time by remember { mutableFloatStateOf(0f) }
    var active by remember { mutableStateOf(true) }
    val owner = LocalLifecycleOwner.current
    DisposableEffect(owner) {
        val o = LifecycleEventObserver { _, e ->
            if (e == Lifecycle.Event.ON_START) active = true
            if (e == Lifecycle.Event.ON_STOP) active = false
        }
        owner.lifecycle.addObserver(o); onDispose { owner.lifecycle.removeObserver(o) }
    }
    if (quality.animated && active) {
        LaunchedEffect(quality) {
            var last = 0L
            while (true) withFrameNanos { n ->
                if (last == 0L) last = n
                val dt = (n - last) / 1e9f
                if (dt >= quality.frameIntervalSec) { time += dt; last = n }
            }
        }
    }
    Canvas(modifier.fillMaxSize()) {
        val t = time
        drawRect(Brush.verticalGradient(listOf(NovauraColors.Background, NovauraColors.Background2, NovauraColors.Background)))
        val w = size.width; val h = size.height
        val cx = w * 0.5f; val cy = h * 0.33f
        val r = min(w, h) * 0.13f
        val par = if (quality.parallax) sin(t * 0.15f) * 6f else 0f
        drawCircle(Brush.radialGradient(listOf(NovauraColors.Violet.copy(alpha = 0.28f), Color.Transparent), Offset(cx, cy), r * 5f), r * 5f, Offset(cx, cy))
        for (i in stars.x.indices) {
            var px = stars.x[i] * w + par * stars.s[i]; var py = stars.y[i] * h
            val dx = px - cx; val dy = py - cy; val d = hypot(dx, dy)
            if (d < r * 3.2f && d > 1f) { val nd = d + r * r * 0.9f / d; px = cx + dx / d * nd; py = cy + dy / d * nd }
            if (d < r * 1.05f) continue
            val a = if (quality.twinkle) 0.35f + 0.65f * (0.5f + 0.5f * sin(t * 1.6f + stars.ph[i])) else 0.75f
            drawCircle(Color.White.copy(alpha = a), stars.s[i], Offset(px, py))
        }
        fun drawDisk(front: Boolean) {
            for (i in disk.a.indices) {
                val ang = disk.a[i] + t * disk.sp[i] / disk.rad[i]
                val sn = sin(ang)
                if ((sn >= 0f) != front) continue
                val x = cx + cos(ang) * disk.rad[i] * r * 1.5f
                val y = cy + sn * disk.rad[i] * r * 0.38f
                drawCircle(diskColors[disk.c[i]].copy(alpha = 0.85f), 1.4f + (3f - disk.rad[i]) * 0.5f, Offset(x, y))
            }
        }
        drawDisk(front = false)
        drawCircle(Brush.radialGradient(listOf(Color.Transparent, NovauraColors.NeonPink.copy(alpha = 0.35f), Color.Transparent), Offset(cx, cy), r * 1.9f), r * 1.9f, Offset(cx, cy))
        drawCircle(Color.Black, r, Offset(cx, cy))
        drawCircle(NovauraColors.LightPink.copy(alpha = 0.9f), r * 1.04f, Offset(cx, cy), style = Stroke(width = 2.5f))
        if (quality.animated) {
            val p = (t * 0.25f) % 1f
            drawCircle(NovauraColors.Teal.copy(alpha = 0.25f * (1f - p)), r * (1.3f + p * 2.2f), Offset(cx, cy), style = Stroke(1.5f))
        }
        drawDisk(front = true)
    }
}
