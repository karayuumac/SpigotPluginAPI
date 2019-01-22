package extension

import data.CannotFindPlayerException
import data.migration.component.Migration
import org.bukkit.entity.Player
import java.util.*

/**
 * @author karayuu
 */
/**
 * [clazz]で指定したクラスのMigrationを取得します.
 */
fun <T: Migration> List<Migration>.find(clazz: Class<T>): T? {
    val list = this
    for (migration in list) {
        if (migration.javaClass == clazz) {
            return migration as T
        }
    }
    return null
}

fun <T: Migration> Map<UUID, List<Migration>>.find(player: Player, clazz: Class<T>): T? {
    val list = this[player.uniqueId] ?: throw CannotFindPlayerException()
    return list.find(clazz)
}
