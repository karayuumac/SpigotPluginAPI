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
     * 該当[uuid]プレイヤーのデータを取得します.
     */
    fun <E> load(clazz: Class<E>, uuid: UUID): E {
        return table.load(clazz, uuid)
    }

    /**
     * 該当[player]のプレイヤーデータの作成を行います.
     * 初参加時にのみ作成されます.
     */
    fun create(player: Player) {
        table.create(player)
    }
}
