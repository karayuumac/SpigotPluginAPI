package extension

import data.migration.component.Migration

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