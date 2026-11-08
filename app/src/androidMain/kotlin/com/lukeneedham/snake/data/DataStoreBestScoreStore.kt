package com.lukeneedham.snake.data

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/** Keeps the best score in a DataStore file. There must be only one instance, which Koin makes sure of. */
class DataStoreBestScoreStore(context: Context) : BestScoreStore {

    private val dataStore = PreferenceDataStoreFactory.create(
        produceFile = { context.applicationContext.preferencesDataStoreFile(fileName) },
    )

    override val best: Flow<Int> = dataStore.data
        .map { it[bestScoreKey] ?: 0 }
        .catch { emit(0) }

    override suspend fun save(score: Int) {
        dataStore.edit { preferences ->
            if (score > (preferences[bestScoreKey] ?: 0)) preferences[bestScoreKey] = score
        }
    }

    private companion object {
        const val fileName = "snake"
        val bestScoreKey = intPreferencesKey("best_score")
    }
}
