package command

import command.data.DataTestCommand
import command.workbench.WorkBenchCommand
import org.bukkit.Bukkit
import org.bukkit.command.TabExecutor

/**
 * Created by karayuu on 2019/01/03
 */
object CommandHandler {
    fun register() {
        registerCommand(WorkBenchCommand, "workbench")
        registerCommand(DataTestCommand, "test")
    }

    private fun registerCommand(executor: TabExecutor, name: String) {
        AutoFarming.plugin.getCommand(name).also {
            if (it == null) {
                Bukkit.getLogger().warning("Command登録処理エラー(${it::class.simpleName})")
            } else {
                it.executor = executor
            }
        }
    }
}
