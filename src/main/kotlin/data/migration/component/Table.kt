package data.migration.component

import config.configs.DatabaseConfig
import data.SqlHandler
import data.SqlSelecter
import org.bukkit.entity.Player
import java.lang.IllegalStateException
import java.util.*

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
    private val builder = SQLCommandBuilder()

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
     * SQLから値をロードします.
     */
    fun <E> load(clazz: Class<E>, uuid: UUID): E {
        return SqlSelecter.selectOne("select * from $db.$table_name where uuid like '?'", clazz, uuid.toString())
    }

    /**
     * 初参加の際に,データを作成します.
     */
    fun create(player: Player) {
        val command = "select count(*) as count from $db.$table_name where uuid = '${player.uniqueId}'"

        val result = SqlHandler.getResult(command)
        val rs = result.first ?:
            throw IllegalStateException("[SQLError] ResultSet is null.")
        val connections = result.second

        var count = -1
        while (rs.next()) {
            count = rs.getInt("count")
        }
        SqlHandler.disconnect(connections)

        if (count == 0) {
            //初見さん
            val insert = "insert into $db.$table_name (name, uuid) values('${player.name}', '${player.uniqueId}')"
            SqlHandler.execute(insert)
        }
    }
}

/**
 * SQLのコマンドを作成可能なBuilderです.
 *
 * @author karayuu
 */
class SQLCommandBuilder {
    private var command: String = ""
    fun add(column_name: String, type: String, default: String): SQLCommandBuilder {
        command += ",add column if not exists $column_name $type default $default"
        return this
    }

    fun add(command: String): SQLCommandBuilder {
        this.command += ",$command"
        return this
    }

    fun build(table_name: String): String {
        return "alter table ${DatabaseConfig.database}.$table_name " + command.removePrefix(",")
    }
}
