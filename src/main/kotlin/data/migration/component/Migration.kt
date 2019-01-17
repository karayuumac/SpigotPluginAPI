package data.migration.component

import java.util.*

/**
 * @author karayuu
 */
open class Migration(table_name: String) {
    open protected val table = Table(table_name)

    /**
     * tableの作成を行います.
     */
    fun migrate() {
        table.make()
    }

    /**
     * 該当[uuid]プレイヤーのデータを取得します.
     */
    fun <E> load(clazz: Class<E>, uuid: UUID): E {
        return table.load(clazz, uuid)
    }
}
