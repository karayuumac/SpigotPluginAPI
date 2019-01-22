package data

import config.configs.DatabaseConfig
import extension.warn
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
     * 非同期下で実行して下さい.
     */
    fun execute(command: String) {
        val pair = connect()
        val statement = pair.second

        try {
            statement.executeUpdate(command)
        } catch (e: SQLException) {
            plugin.warn("[SQLError] Can't execute sql query.")
            e.printStackTrace()
        } finally {
            disconnect(pair)
        }
    }

    /**
     * [command] で入力されたSQLコマンドを実行し,[ResultSet?]を返します.
     * 非同期下で実行して下さい.
     */
    fun getResult(command: String): ResultSet? {
        var rs: ResultSet? = null
        val pair = connect()
        val statement = pair.second

        try {
            rs = statement.executeQuery(command)
        } catch (e: SQLException) {
            plugin.warn("[SQLError] Can't execute sql query.")
            e.printStackTrace()
        } finally {
            disconnect(pair)
        }
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

    private fun disconnect(pair: Pair<Connection, Statement>) {
        disconnect(pair.first, pair.second)
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
object SolSelector {
    /**
     * selectを実行する関数です.
     * [sql] でsql文を(?はプレースホルダー),[clazz] で格納するclassを指定します.
     *
     * [SqlHandler.getResult]でnullが返された時,[IllegalStateException]を投げます.
     * 非同期下で実行して下さい.
     */
    fun <E> selectOne(sql: String, clazz: Class<E>, vararg params: String): E {
        val obj: E
        for (param in params) {
            sql.replaceFirst("?", param)
        }

        val rs = SqlHandler.getResult(sql) ?:
            throw IllegalStateException("[SQLError] ResultSet is null.(SqlSelector#selectOne")

        obj = toObject(rs, clazz)
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
