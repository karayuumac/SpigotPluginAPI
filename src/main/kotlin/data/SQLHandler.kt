package data

import config.configs.DatabaseConfig
import extension.warn
import org.bukkit.Bukkit
import java.sql.*

/**
 * SQLの初期準備等を行うオブジェクトです.
 *
 * @author karayuu
 */
object SQLHandler {
    private val plugin = AutoFarming.plugin

    lateinit var connection: Connection
    private lateinit var statement: Statement

    private val url = DatabaseConfig.url
    private val user = DatabaseConfig.user
    private val password = DatabaseConfig.password

    init {
        //Taskを生成していないが,処理上最優先(connection, statement生成)＆負荷も軽いため,メインスレッドで行う.
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
        AutoFarming.runTaskAsynchronously(Runnable {
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
        })
    }

    /**
     * [command] で入力されたSQLコマンドを実行します.
     */
    fun execute(command: String) {
        AutoFarming.runTaskAsynchronously(Runnable {
            checkConnection()
            try {
                statement.executeUpdate(command)
            } catch (e: SQLException) {
                plugin.warn("[SQLError] Can't execute sql query.")
                e.printStackTrace()
            }
        })
    }

    /**
     * [connection],[statement] を切断します.
     * Disabled時に呼び出されることを想定しています.
     * 保存処理後,1度呼び出すことを推奨します.
     */
    fun disconnect() {
        if (!connection.isClosed || !statement.isClosed) {
            connection.close()
            statement.close()
        }
    }

    private fun createConnection(): Connection {
        return DriverManager.getConnection(url, user, password)
    }
}

/**
 * SQLにおいて,特にカラムの操作を行う便利な関数をまとめたオブジェクトです.
 * https://qiita.com/momosetkn/items/c5d420d780ae7b4d401f
 * を利用させていただきました.
 *
 * @author karayuu
 */
object SqlSelecter {
    /**
     * selectを実行する関数です.
     * [sql] でsql文を(?はプレースホルダー),[clazz] で格納するclassを指定します.
     */
    fun <E> selectOne(sql: String, clazz: Class<E>, vararg params: String): E {
        var obj: E = clazz.newInstance()

        AutoFarming.runTaskAsynchronously(Runnable {
            val statement: Statement = SQLHandler.connection.createStatement()
            val rs: ResultSet

        /*try { } catch (e: SQLException) {
            plugin.warn("[SQLError] Can't create statement.")
            e.printStackTrace()
        }*/
            for (param in params) {
                sql.replaceFirst("?", param)
            }
            rs = statement.executeQuery(sql)
            obj = toObject(rs, clazz)

            rs.close()
            statement.close()
        })
        return obj
    }

    /**
     * [rs] から [clazz] のオブジェクトを生成します.
     */
    private fun <E> toObject(rs: ResultSet, clazz: Class<E>): E {
        val bean = clazz.newInstance()
        if (rs.next()) {
            val fields = clazz.fields
            for (field in fields) {
                val obj = rs.getObject(field.name)
                field.set(bean, obj)
            }
        }
        return bean
    }
}
