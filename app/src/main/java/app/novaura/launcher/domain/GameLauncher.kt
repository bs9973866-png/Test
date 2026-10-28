package app.novaura.launcher.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class Profile(val id: String, val name: String, val mcVersion: String, val loader: String)

sealed interface LaunchEvent {
    data class Progress(val stage: String, val fraction: Float) : LaunchEvent
    data object Started : LaunchEvent
    data class Failed(val message: String) : LaunchEvent
}

/**
 * Точка интеграции рантайма запуска (PojavLauncher-совместимое ядро: JRE, LWJGL-обёртка,
 * графический backend). Реализация подключается на следующем этапе.
 */
interface GameLauncher { fun launch(profile: Profile): Flow<LaunchEvent> }

/** Честная заглушка: ничего не имитирует, а сообщает, что рантайм не подключён. */
class RuntimeNotIntegratedLauncher : GameLauncher {
    override fun launch(profile: Profile): Flow<LaunchEvent> = flow {
        emit(LaunchEvent.Failed("Рантайм запуска Minecraft ещё не подключён к этой сборке."))
    }
}
