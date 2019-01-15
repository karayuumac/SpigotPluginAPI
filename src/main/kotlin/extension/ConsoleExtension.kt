package extension

import AutoFarming
import org.bukkit.ChatColor

/**
 * コンソールメッセージに関する拡張関数をまとめたファイルです.
 *
 * @author karayuu
 */

fun AutoFarming.warn(message: String) {
    this.logger.warning("${ChatColor.RED}$message")
}