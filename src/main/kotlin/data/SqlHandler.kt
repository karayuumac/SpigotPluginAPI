package data

import config.configs.DatabaseConfig
import data.migration.component.Migration
import extension.info
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
     * [command] で入力されたSQLコマンドを実行し[column_names]に応じて,
     * [Map<String(カラム名), Any(そのデータ)>]を返します.
     * 非同期下で実行して下さい.
     */
    fun getResult(command: String, column_names: List<String>): Map<String, Any> {
        val pair = connect()
        val statement = pair.second

        val result = mutableMapOf<String, Any>()

        try {
            val rs = statement.executeQuery(command)
            while (rs.next()) {
                for (name in column_names) {

                    result[name] = rs.getObject(name)
                }
            }
        } catch (e: SQLException) {
            plugin.warn("[SQLError] Can't execute sql query.")
            e.printStackTrace()
        } finally {
            disconnect(pair)
        }

        result.forEach { t, u -> AutoFarming.plugin.info("$t is $u") }

        return result.toMap()
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
object SqlSelector {
    /**
     * selectを実行する関数です.
     * [sql] でsql文を(?はプレースホルダー),[clazz] で格納するclassを指定します.
     *
     * [SqlHandler.getResult]でnullが返された時,[IllegalStateException]を投げます.
     * 非同期下で実行して下さい.
     */
    fun <E: Migration> selectOne(sql: String, clazz: Class<E>, vararg params: String): E {
        val obj: E
        for (param in params) {
            sql.replaceFirst("?", param)
        }

        AutoFarming.plugin.info("ここはどうかな？")
        AutoFarming.plugin.info(clazz.fields.joinToString { it.name })
        clazz.fields.map { it.name }.forEach { AutoFarming.plugin.info(it) }

        val result = SqlHandler.getResult(sql, clazz.fields.map { it.name })

        result.forEach { t, u -> AutoFarming.plugin.info("$t is $u") }

        obj = toObject(result, clazz)
        return obj
    }

    /**
     * [rs] から [clazz] のオブジェクトを生成します.
     */
    private fun <E: Migration> toObject(rm: Map<String, Any>, clazz: Class<E>): E {
        val bean = clazz.newInstance()
        val fields = clazz.fields
        for (field in fields) {
            //設計上,Valueが存在しないことはありえないため,強制non-null化.
            val obj = rm[field.name]!!
            field.set(bean, obj)
        }
        return bean
    }
}
