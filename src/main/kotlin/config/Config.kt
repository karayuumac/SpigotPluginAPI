package config

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

/**
 * @author karayuu
 */
open class Config(name: String) {
    private val configName: String = "$name.yml"
    open var config = File(AutoFarming.plugin.dataFolder, configName)
    open lateinit var fileConfiguration: FileConfiguration

    /**
     * Configファイルの登録関数です.
     * 登録する際に1度呼び出して下さい.
     */
    fun register() {
        makeFile()
        load()
    }

    /**
     * Configのファイルを作成します.
     * この段階では[fileConfiguration]は生成されていません.
     */
    protected open fun makeFile() {
        if (!config.exists()) {
            AutoFarming.plugin.saveResource(configName, false)
        }
    }

    /**
     * Configを読み込みます.
     * この段階で[fileConfiguration]が読み込まれます.
     */
    protected open fun load() {
        fileConfiguration = YamlConfiguration.loadConfiguration(config)
    }
}