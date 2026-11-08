package com.lukeneedham.snake.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukeneedham.snake.data.SettingsStore
import com.lukeneedham.snake.game.SprintSide
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(isDebugEnabled: Boolean, private val settingsStore: SettingsStore) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState(isDebugEnabled = isDebugEnabled))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsStore.sprintSide.collect { side -> _uiState.update { it.copy(sprintSide = side) } }
        }
    }

    fun onSprintSide(side: SprintSide) {
        viewModelScope.launch { settingsStore.setSprintSide(side) }
    }
}
