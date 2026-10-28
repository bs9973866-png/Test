package app.novaura.launcher.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.novaura.launcher.data.SettingsRepository
import app.novaura.launcher.domain.*
import app.novaura.launcher.ui.cosmos.CosmosQuality
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class GameState { IDLE, PREPARING, RUNNING, ERROR }
data class HomeUi(val state: GameState = GameState.IDLE, val stage: String = "", val progress: Float = 0f, val error: String? = null)

class HomeViewModel(app: Application) : AndroidViewModel(app) {
    private val settings = SettingsRepository(app)
    private val launcher: GameLauncher = RuntimeNotIntegratedLauncher()
    val profile = Profile("default", "Мой профиль", "1.20.1", "Fabric")

    val quality = settings.quality.stateIn(viewModelScope, SharingStarted.Eagerly, CosmosQuality.BALANCED)
    private val _ui = MutableStateFlow(HomeUi())
    val ui: StateFlow<HomeUi> = _ui
    private var job: Job? = null

    fun setQuality(q: CosmosQuality) = viewModelScope.launch { settings.setQuality(q) }

    fun play() {
        if (_ui.value.state == GameState.PREPARING || _ui.value.state == GameState.RUNNING) return
        _ui.value = HomeUi(GameState.PREPARING, "Подготовка…")
        job = viewModelScope.launch {
            launcher.launch(profile).collect { e ->
                _ui.value = when (e) {
                    is LaunchEvent.Progress -> HomeUi(GameState.PREPARING, e.stage, e.fraction)
                    LaunchEvent.Started -> HomeUi(GameState.RUNNING, "Игра запущена")
                    is LaunchEvent.Failed -> HomeUi(GameState.ERROR, error = e.message)
                }
            }
        }
    }
    fun cancel() { job?.cancel(); _ui.value = HomeUi() }
}
