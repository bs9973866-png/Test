package app.novaura.launcher.ui.cosmos

enum class CosmosQuality(val title: String, val stars: Int, val diskParticles: Int, val animated: Boolean, val parallax: Boolean, val twinkle: Boolean, val frameIntervalSec: Float) {
    OFF("Выкл.", 0, 0, false, false, false, 1f),
    LOW("Низкое", 70, 90, true, false, false, 1f / 20f),
    BALANCED("Сбалансированное", 140, 180, true, true, true, 1f / 30f),
    HIGH("Высокое", 240, 320, true, true, true, 1f / 45f),
    ULTRA("Ультра", 380, 520, true, true, true, 1f / 60f);
    companion object { fun fromName(name: String?): CosmosQuality = entries.firstOrNull { it.name == name } ?: BALANCED }
}
