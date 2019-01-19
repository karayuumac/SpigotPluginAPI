package data

import config.configs.DatabaseConfig
import extension.warn
import java.lang.IllegalStateException
import java.sql.*

/**
 * SQLの初期準備等を行うオブジェクトです.
 *
 * @author karayuu
 */
object SQLHandler {
    private val plugin = AutoFarming.plugin

    private val url = DatabaseConfig.url
    private val user = DatabaseConfig.user
    private val password = DatabaseConfig.password

    /**
     * [command] で入力されたSQLコマンドを実行します.
     */
    fun execute(command: String) {
        AutoFarming.runTaskAsynchronously(Runnable {
            val (connection, statement) = connect()

            try {
                statement.executeUpdate(command)
            } catch (e: SQLException) {
                plugin.warn("[SQLError] Can't execute sql query.")
                e.printStackTrace()
            } finally {
                disconnect(connection, statement)
            }
        })
    }

    /**
     * [command] で入力されたSQLコマンドを実行し,[ResultSet]を返します(nullable).
     */
    fun getResult(command: String): ResultSet? {
        var rs: ResultSet? = null
        AutoFarming.runTaskAsynchronously(Runnable {
            val (connection, statement) = connect()

            try {
                rs = statement.executeQuery(command)
            } catch (e: SQLException) {
                plugin.warn("[SQLError] Can't execute sql query.")
                e.printStackTrace()
            } finally {
                disconnect(connection, statement)
            }
        })
        return rs
    }

    /**
     * [connection],[statement] を切断します.
     */
    private fun disconnect(connection: Connection, statement: Statement) {
        if (!connection.isClosed || !statement.isClosed) {
            connection.close()
            statement.close()
        }
    }

    /**
     * connection, statementを生成します.
     */
    private fun connect(): Pair<Connection, Statement> {
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
     * [SQLHandler.getResult]でnullが返された時,[IllegalStateException]を投げます.
     */
    fun <E> selectOne(sql: String, clazz: Class<E>, vararg params: String): E {
        var obj: E = clazz.newInstance()

        AutoFarming.runTaskAsynchronously(Runnable {
            for (param in params) {
                sql.replaceFirst("?", param)
            }
            val rs: ResultSet = SQLHandler.getResult(sql) ?:
                throw IllegalStateException("[SQLError] ResultSet is null.")

            obj = toObject(rs, clazz)
        })
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
