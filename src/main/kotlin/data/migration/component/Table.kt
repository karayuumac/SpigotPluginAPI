package data.migration.component

import config.configs.DatabaseConfig
import data.SQLHandler

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
    val builder = SQLCommandBuilder()

    init {
        val command = "create table if not exists ${DatabaseConfig.database}.$table_name (" +
                "table_name varchar(30) unique," +
                "uuid varchar(128) primary key)"
        SQLHandler.execute(command)
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
        SQLHandler.execute(command)
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