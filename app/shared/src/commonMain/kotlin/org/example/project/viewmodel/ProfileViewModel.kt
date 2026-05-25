package org.example.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.model.Todo

sealed class ProfileUIState {
    object Idle : ProfileUIState()
    object Loading : ProfileUIState()
    data class Success(val todos: List<Todo>) : ProfileUIState()
    data class Error(val message: String) : ProfileUIState()
}

class ProfileViewModel: ViewModel() {
    private val _uiState = MutableStateFlow<ProfileUIState>(ProfileUIState.Idle)
    val uiState: StateFlow<ProfileUIState> = _uiState.asStateFlow()

    fun resetState() {
        _uiState.value = ProfileUIState.Idle
    }

    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    private val baseUrl = "http://localhost:9090"

    fun loadTodos() {
        viewModelScope.launch {
            _uiState.value = ProfileUIState.Loading
            try {
                val response = client.get("$baseUrl/todos")
                val todos = response.body<List<Todo>>()
                _uiState.value = ProfileUIState.Success(todos)
            } catch (e: Exception) {
                _uiState.value = ProfileUIState.Error("Error loading todos: ${e.message}")
            }
        }
    }

    override fun onCleared() {
        client.close()
    }
}