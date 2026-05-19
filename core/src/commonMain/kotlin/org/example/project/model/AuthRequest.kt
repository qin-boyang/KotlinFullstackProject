package org.example.project.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    @SerialName("email") val username:String,
    @SerialName("password") val password:String,
    @SerialName("role") val role: String = "APP_USER")
