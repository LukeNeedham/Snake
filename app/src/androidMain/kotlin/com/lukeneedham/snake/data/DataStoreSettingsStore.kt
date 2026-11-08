package com.lukeneedham.snake.data

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.lukeneedham.snake.game.SprintSide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/** Keeps the settings in a DataStore file of their own. There must be only one instance, which Koin makes sure of. */
class DataStoreSettingsStore(context: Context) : SettingsStore {

    private val dataStore = PreferenceDataStoreFactory.create(
        produceFile = { context.applicationContext.preferencesDataStoreFile(fileName) },
    )

    override val sprintSide: Flow<SprintSide> = dataStore.data
        .map { preferences -> SprintSide.entries.firstOrNull { it.name == preferences[sprintSideKey] } ?: SprintSide.Left }
        .catch { emit(SprintSide.Left) }

    override suspend fun setSprintSide(side: SprintSide) {
        dataStore.edit { it[sprintSideKey] = side.name }
    }

    private companion object {
        const val fileName = "settings"
        val sprintSideKey = stringPreferencesKey("sprint_side")
    }
}
