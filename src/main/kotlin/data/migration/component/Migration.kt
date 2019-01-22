package data.migration.component

import org.bukkit.entity.Player
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
     * 該当[player]のプレイヤーデータの作成を行います.
     * 初参加時にのみ作成されます.
     * 新規参加の有無を問わず,データを返します.
     */
    fun <T: Migration> createAndLoad(player: Player, clazz: Class<T>): T? {
        return table.createAndLoad(player, clazz)
    }
}
