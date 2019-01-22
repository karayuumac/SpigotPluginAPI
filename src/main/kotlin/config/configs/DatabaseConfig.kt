package config.configs

import config.Config

/**
 * @author karayuu
 */
object DatabaseConfig : Config("database") {
    val database: String by lazy { fileConfiguration.getString("db") }
    val user: String by lazy { fileConfiguration.getString("user") }
    val password: String by lazy { fileConfiguration.getString("password") }
    val url: String by lazy {
        "jdbc:mysql://${fileConfiguration.getString("host")}:" +
                fileConfiguration.getString("port")
    }
}
