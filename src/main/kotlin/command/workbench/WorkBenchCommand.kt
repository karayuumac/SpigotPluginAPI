package command.workbench

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

/**
 * Created by karayuu on 2019/01/03
 */
object WorkBenchCommand : TabExecutor {
    override fun onCommand(sender: CommandSender?, command: Command?, string: String?, args: Array<out String>?): Boolean {
        if (sender !is Player) return false

        sender.openWorkbench(null, true)
        return true
    }

    override fun onTabComplete(
        p0: CommandSender?,
        p1: Command?,
        p2: String?,
        p3: Array<out String>?
    ): MutableList<String> = mutableListOf()
}
