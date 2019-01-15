package data.migration.component

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
}