package recipe

import org.bukkit.Bukkit

/**
 * Created by karayuu on 2019/01/28
 */
object RecipeHandler {
    /**
     * 追加するレシピを指定します.
     */
    private val recipies = listOf<>(

    )

    fun register() {
        recipies.forEach {
            val recipe = it.build()
            Bukkit.addRecipe(recipe)
        }
    }
}
