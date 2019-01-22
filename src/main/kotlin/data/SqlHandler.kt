package data

import AutoFarming.Companion.runTaskAsynchronously
import config.configs.DatabaseConfig
import extension.info
import extension.wait
import extension.warn
import java.lang.IllegalStateException
import java.sql.*

/**
 * SQLの初期準備等を行うオブジェクトです.
 *
 * @author karayuu
 */
object SqlHandler {
    private val plugin = AutoFarming.plugin

    private val url = DatabaseConfig.url
    private val user = DatabaseConfig.user
    private val password = DatabaseConfig.password

    /**
     * [command] で入力されたSQLコマンドを実行します.
     */
    fun execute(command: String) {
        runTaskAsynchronously(Runnable {
            val connections = connect()
            val statement = connections.second

            try {
                statement.executeUpdate(command)
            } catch (e: SQLException) {
                plugin.warn("[SQLError] Can't execute sql query.")
                e.printStackTrace()
            } finally {
                disconnect(connections)
            }
        })
    }

    /**
     * [command] で入力されたSQLコマンドを実行し,[list]をもとに,
     * [Map<String(カラム名), Any(そのデータ)>]を返します.
     * [list]には[list<String(カラム名)]を入力してください.
     */
    fun getResult(command: String, list: List<String>): Map<String, Any> {
        val result = mutableMapOf<String, Any>()

        runTaskAsynchronously(Runnable {
            val connections = connect()
            val statement = connections.second

            try {
                val rs = statement.executeQuery(command)
                AutoFarming.runTaskLater(Runnable {
                    while (rs.next()) {
                        for (name in list) {
                            result[name] = rs.getObject(name)
                        }
                    }
                }, 60)
                plugin.info("sql実行(getResult)")
            } catch (e: SQLException) {
                plugin.warn("[SQLError] Can't execute sql query.")
                e.printStackTrace()
            } finally {
                disconnect(connections)
            }
        })
        return result
    }

    /**
     * [connection],[statement] を切断します.
     */
    fun disconnect(connections: Pair<Connection?, Statement?>) {
        val connection = connections.first ?: return
        val statement = connections.second ?: return

        if (!connection.isClosed || !statement.isClosed) {
            connection.close()
            statement.close()
        }
    }

    /**
     * connection, statementを生成します.
     */
    fun connect(): Pair<Connection, Statement> {
        val connection = createConnection()
        val statement = connection.createStatement()
        return Pair(connection, statement)
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
     *
     * [SqlHandler.getResult]でnullが返された時,[IllegalStateException]を投げます.
     */
    fun <E> selectOne(sql: String, clazz: Class<E>, vararg params: String): E {
        var obj: E = clazz.newInstance()
        var hasFinished = false

        runTaskAsynchronously(Runnable {
            for (param in params) {
                sql.replaceFirst("?", param)
            }

            val connections = SqlHandler.connect()
            val statement = connections.second

            val rs = statement.executeQuery(sql)

            obj = toObject(rs, clazz)
            hasFinished = true
        })

        hasFinished.wait(true)

        return obj
    }

    /**
     * [rs] から [clazz] のオブジェクトを生成します.
     */
    private fun <E> toObject(rs: ResultSet, clazz: Class<E>): E {
        val bean = clazz.newInstance()
        while (rs.next()) {
            val fields = clazz.fields
            for (field in fields) {
                val obj = rs.getObject(field.name)
                field.set(bean, obj)
            }
        }
        return bean
    }
}
