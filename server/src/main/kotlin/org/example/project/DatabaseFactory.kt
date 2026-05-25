package org.example.project

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.h2.tools.Server
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        // Start H2 Web Console on port 8082
        Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start()

        Database.connect(createHikariDataSource())
        transaction {
            SchemaUtils.create(Users)
            // Seed data if empty
            if (Users.selectAll().empty()) {
                Users.insert {
                    it[username] = "qboyang"
                    it[password] = "123456"
                }
            }
            SchemaUtils.create(Todos)
            if (Todos.selectAll().empty()) {
                Todos.insert {
                    it[title] = "Learn Exposed"
                    it[completed] = false
                }
                Todos.insert {
                    it[title] = "Learn Kotlin"
                    it[completed] = false
                }
            }
        }
    }

    private fun createHikariDataSource(): HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = "org.h2.Driver"
            jdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;"
            username = "sa"
            password = ""
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(config)
    }
}

object Users : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50).uniqueIndex()
    val password = varchar("password", 50)
    override val primaryKey = PrimaryKey(id)
}

object Todos : Table() {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 50)
    val completed = bool("completed")
    override val primaryKey = PrimaryKey(id)
}