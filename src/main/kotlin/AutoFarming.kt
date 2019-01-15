import command.CommandHandler
import config.ConfigHandler
import data.migration.TableMigrator
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

/**
 * Created by karayuu on 2019/01/02
 */
class AutoFarming : JavaPlugin() {
    override fun onEnable() {
        plugin = this

        Bukkit.getPluginManager().registerEvents(PlayerInteractListener, this)

        CommandHandler.register()

        ConfigHandler.register()

        //テーブル作成を請け負う.
        TableMigrator.migrate()
    }

    companion object {
        lateinit var plugin: AutoFarming
    }
}

object PlayerInteractListener : Listener {
    @EventHandler
    fun onPlayerRightclickCrop(e: PlayerInteractEvent) {
        val player = e.player ?: return
        val block = e.clickedBlock ?: return
        val world = block.world

        if (e.action != Action.RIGHT_CLICK_BLOCK) return
        if (block.type != Material.WHEAT) return
        if (block.data != 7.toByte()) return

        val age = block.blockData as Ageable
        age.age = 0
        block.blockData = age

        world.dropItem(block.location, ItemStack(Material.WHEAT, 1))
    }
}
