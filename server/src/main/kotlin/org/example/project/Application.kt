package org.example.project

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.project.model.AuthRequest

fun main() {
    embeddedServer(Netty, port = 9090, host = "localhost", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    routing {
        get("/") {
            call.respondText(sayHello("Ktor"))
        }
        get("/hello") {
            val name = call.request.queryParameters["name"] ?: "Anonymous"
            call.respondText(sayHello(name))
        }
        post("/auth/authenticate") {
            val request = call.receive<AuthRequest>()
            call.respondText("${request.username} ${request.password} is received")
        }
        delete("/delete/{username}") {
            val username = call.parameters["username"]
            call.respondText("$username is deleted")
        }
    }
}
