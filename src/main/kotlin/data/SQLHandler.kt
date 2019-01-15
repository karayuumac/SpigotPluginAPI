package data

import config.configs.DatabaseConfig
import extension.warn
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

/**
 * SQLの初期準備等を行うオブジェクトです.
 *
 * @author karayuu
 */
object SQLHandler {
    private val plugin = AutoFarming.plugin

    private lateinit var connection: Connection
    private lateinit var statement: Statement

    private val url = DatabaseConfig.url
    private val user = DatabaseConfig.user
    private val password = DatabaseConfig.password

    init {
        try {
            connection = createConnection()
            statement = connection.createStatement()
        } catch (e: SQLException) {
            e.printStackTrace()
            plugin.warn("[SQLError] Initialization hasn't been succeed." +
                    "Check your DatabaseConfig to confirm the requirements.")
        }
    }

    /**
     * SQLの接続を確認します.
     */
    fun checkConnection() {
        try {
            if (connection.isClosed) {
                plugin.warn("[SQLError] Connection is closed. Reconnecting...")
                connection = createConnection()
            }
            if (statement.isClosed) {
                plugin.warn("[SQLError] Statement is closed. Reconnecting...")
                statement = connection.createStatement()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            plugin.warn("[SQLError] Can't connect to the SQL." +
                    "Check your DatabaseConfig to confirm the requirements.")
        }
    }

    /**
     * [command] で入力されたSQLコマンドを実行します.
     */
    fun execute(command: String) {
        checkConnection()
        try {
            statement.executeUpdate(command)
        } catch (e: SQLException) {
            plugin.warn("[SQLError] Can't execute sql query.")
            e.printStackTrace()
        }
    }

    private fun createConnection(): Connection {
        return DriverManager.getConnection(url, user, password)
    }
}