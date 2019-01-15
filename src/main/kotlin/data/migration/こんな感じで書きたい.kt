package data.migration

import data.migration.component.Migration

/**
 * @author karayuu
 */

class Create_user_table : Migration("user_table_mining") {
    var mining_all = table.int("mining_all")
}

object TableMigrator {
    /** Migrationを行うクラスを指定します. */
    private val tables = listOf(
        Create_user_table::class.java
    )

    /**
     * [tables]で指定した全てのテーブルの作成を行います.
     */
    fun migrate() {
        tables.forEach {
            //インスタンス作成,migrate呼び出しにより,migrationが実行される.
            it.newInstance().migrate()
        }
    }
}