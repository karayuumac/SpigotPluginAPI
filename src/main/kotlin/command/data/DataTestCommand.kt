package command.data

import data.migration.migrations.Create_user_table
import extension.find
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

/**
 * Created by karayuu on 2019/01/20
 */
object DataTestCommand : TabExecutor {
    override fun onCommand(p0: CommandSender?, p1: Command?, p2: String?, p3: Array<out String>?): Boolean {
        if (p0 is Player) {
            p0.sendMessage("" + AutoFarming.playerData.find(p0, Create_user_table::class.java)?.mining_all)
        }
        return true
    }

    override fun onTabComplete(
        p0: CommandSender?,
        p1: Command?,
        p2: String?,
        p3: Array<out String>?
    ): MutableList<String> = mutableListOf()
}
