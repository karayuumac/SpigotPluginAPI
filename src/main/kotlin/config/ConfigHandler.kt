package config

import config.configs.DatabaseConfig

/**
 * @author karayuu
 */
object ConfigHandler {
    val configs = listOf(
        DatabaseConfig
    )

    fun register() {
        configs.forEach { conf ->
            conf.register()
        }
    }
}