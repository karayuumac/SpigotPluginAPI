package extension

import AutoFarming
import org.bukkit.ChatColor

/**
 * コンソールメッセージに関する拡張関数をまとめたファイルです.
 *
 * @author karayuu
 */

fun AutoFarming.warn(message: String) {
    this.server.consoleSender.sendMessage("${ChatColor.RED}$message")
}

fun AutoFarming.info(message: String) {
    this.server.consoleSender.sendMessage("${ChatColor.YELLOW}$message")
}
