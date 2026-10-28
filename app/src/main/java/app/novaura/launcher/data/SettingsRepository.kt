package app.novaura.launcher.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import app.novaura.launcher.ui.cosmos.CosmosQuality
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.store by preferencesDataStore("novaura_settings")

class SettingsRepository(private val ctx: Context) {
    private val kQuality = stringPreferencesKey("cosmos_quality")
    val quality: Flow<CosmosQuality> = ctx.store.data.map { CosmosQuality.fromName(it[kQuality]) }
    suspend fun setQuality(q: CosmosQuality) { ctx.store.edit { it[kQuality] = q.name } }
}
