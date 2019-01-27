package data.migration.migrations

import data.migration.component.Migration

/**
 * @author karayuu
 */

class Create_user_table : Migration("user_table_mining") {
    var mining_all = table.int("mining_all")
}
