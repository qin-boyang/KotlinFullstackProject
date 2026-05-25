package org.example.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.model.AuthRequest

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val message: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }

    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    // iOS simulator connecting to local ktor server url
    private val baseUrl = "http://localhost:9090"

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val response = client.post("$baseUrl/auth/authenticate") {
                    contentType(ContentType.Application.Json)
                    setBody(AuthRequest(username, password))
                }

                if (response.status == HttpStatusCode.OK) {
                    _uiState.value = LoginUiState.Success("Welcome, $username!")

                } else {
                    val errorBody = response.bodyAsText()
                    _uiState.value = LoginUiState.Error("Server returned ${response.status.value}: $errorBody")
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error("Connection Failed: ${e.message}")
            }
        }
    }

    override fun onCleared() {
        client.close()
    }
}
