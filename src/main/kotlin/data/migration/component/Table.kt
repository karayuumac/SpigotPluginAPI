package data.migration.component

import config.configs.DatabaseConfig
import data.SqlHandler
import data.SqlSelecter
import extension.info
import extension.wait
import extension.waitChnage
import extension.warn
import org.bukkit.entity.Player
import java.sql.SQLException

/**
 * Tableを表すクラスです.
 * 全てのテーブルは
 * ```
 * table_name : varchar(30) unique
 * uuid : varchar(128) primary key
 * ```
 * をカラムに持ちます.
 *
 * @author karayuu
 */
class Table(val table_name: String) {
    private val builder = SqlCommandBuilder()

    private val db = DatabaseConfig.database

    init {
        val command = "create table if not exists $db.$table_name (" +
                "name varchar(30) unique," +
                "uuid varchar(128) primary key)"
        SqlHandler.execute(command)
    }

    /**
     * varchar型のカラム定義関数です.
     * [name]でカラム名を,[byte]で文字列の長さを指定します.
     * default値はnullになります.
     */
    fun varchar(name: String, byte: Int): String {
        builder.add(name, "varchar($byte)", "null")
        return ""
    }

    /**
     * boolean型のカラム定義関数です.
     * [name]でカラム名を指定します.
     * default値はfalseになります.
     */
    fun boolean(name: String): Boolean {
        builder.add(name, "boolean", "false")
        return false
    }

    /**
     * int型のカラム定義関数です.
     * [name]でカラム名を指定します.
     * default値は0になります.
     */
    fun int(name: String): Int {
        builder.add(name, "int", "0")
        return 0
    }

    /**
     * テーブルを作成します.
     */
    fun make() {
        val command = builder.build(table_name)
        SqlHandler.execute(command)
    }

    /**
     * データを作成し,初見ならデータを作成します.
     * そのデータまたは,新規作成されたデータを返します.
     */
    fun <T: Migration> createAndLoad(player: Player, clazz: Class<T>): T {
        var count = -1
        AutoFarming.runTaskAsynchronously(Runnable {
            val command = "select count(*) as count from $db.$table_name where uuid = '${player.uniqueId}'"

            val (connection, statement) = SqlHandler.connect()

            try {
                val rs = statement.executeQuery(command)
                while (rs.next()) {
                    count = rs.getInt("count")
                }
            } catch (e: SQLException) {
                AutoFarming.plugin.warn("[SQLError] Can't execute sql query.")
                e.printStackTrace()
            }

            SqlHandler.disconnect(Pair(connection, statement))
        })

        count.waitChnage(-1)

        return if (count == 0) {
            //初見さん
            val insert = "insert into $db.$table_name (name, uuid) values('${player.name}', '${player.uniqueId}')"
            SqlHandler.execute(insert)

            clazz.newInstance()
        } else {
            //初見さんじゃないとき
            SqlSelecter.selectOne("select * from $db.$table_name where uuid like '?'",
                clazz, player.uniqueId.toString())
        }
    }
}

/**
 * SQLのコマンドを作成可能なBuilderです.
 *
 * @author karayuu
 */
class SqlCommandBuilder {
    private var command: String = ""
    fun add(column_name: String, type: String, default: String): SqlCommandBuilder {
        command += ",add column if not exists $column_name $type default $default"
        return this
    }

    fun add(command: String): SqlCommandBuilder {
        this.command += ",$command"
        return this
    }

    fun build(table_name: String): String {
        return "alter table ${DatabaseConfig.database}.$table_name " + command.removePrefix(",")
    }
}
