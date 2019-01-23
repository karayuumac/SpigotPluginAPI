package data

import config.configs.DatabaseConfig
import data.migration.component.Migration
import data.migration.component.SqlCommandBuilder
import extension.info
import extension.warn
import java.sql.*
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

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
        AutoFarming.plugin.info("column_names is $column_names")

        try {
            val rs = statement.executeQuery(command)
            while (rs.next()) {
                for (name in column_names) {
                    val temp = rs.getObject(name)
                    result[name] = temp
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
    fun <T: Migration> selectOne(sql: String, clazz: KClass<out T>, vararg params: String): T {
        val obj: T
        var command = sql
        for (param in params) {
            //replaceFirstは新しいStringを返すだけで,副作用を生まないことに注意する!
            command = sql.replaceFirst("?", param)
        }

        AutoFarming.plugin.info("sql is $command")

        //Migrationの継承により,tableがプロパティに含まれてしまう.
        val result = SqlHandler.getResult(command, clazz.memberProperties.map { it.name }
            .filterNot { it == "table" })

        AutoFarming.plugin.info(clazz.memberProperties.map { it.name }
            .filterNot { it == "table" }.toString())

        obj = toObject(result, clazz)
        return obj
    }

    /**
     * [rm] から [clazz] のオブジェクトを生成します.
     */
    private fun <T: Migration> toObject(rm: Map<String, Any>, clazz: KClass<out T>): T {
        val bean = clazz.createInstance()
        val fields = clazz.memberProperties.map { it.javaField }
        for (field in fields) {
            //設計上,Valueが存在しないことはありえないため,強制non-null化.
            field!!.isAccessible = true
            val name = field.name
            if (name == "table") {
                //Migrationの継承により,tableがプロパティに含まれてしまう.
                continue
            }
            //設計上,Valueが存在しないことはありえないため,強制non-null化.
            val obj = rm[field.name]!!

            AutoFarming.plugin.info("Field $name に $obj をセット")
            field.set(bean, obj)
        }
        return bean
    }

    /**
     * [uuid]のデータを[migration]でupdateします.
     * 非同期下で実行して下さい.
     */
    fun update(migration: Migration, uuid: UUID) {
        val builder = SqlCommandBuilder()
        val fields = migration::class.memberProperties

        for (field in fields) {
            if (field.name == "table") {
                //Migrationの継承により,tableがプロパティに含まれてしまう.
                continue
            }
            //builder.update(field.name, field.tyfield.get(migration))
        }
    }
}
