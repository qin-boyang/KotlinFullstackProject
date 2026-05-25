package org.example.project

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.example.project.model.AuthRequest

fun main() {
    embeddedServer(Netty, port = 9090, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()
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
            val user = transaction {
                Users.selectAll().where { Users.username eq request.username }.singleOrNull()
            }

            if (user != null && user[Users.password] == request.password) {
                call.respondText("${request.username} is authenticated")
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
            }
        }
        delete("/delete/{username}") {
            val username = call.parameters["username"]
            call.respondText("$username is deleted")
        }
    }
}
