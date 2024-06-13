package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MiniPlayerViewModel : ViewModel() {
    private val _showMiniPlayer = MutableStateFlow(false)
    val showMiniPlayer: StateFlow<Boolean> = _showMiniPlayer

    fun setShowMiniPlayer(value: Boolean) {
        viewModelScope.launch {
            _showMiniPlayer.emit(value)
        }
    }
}
