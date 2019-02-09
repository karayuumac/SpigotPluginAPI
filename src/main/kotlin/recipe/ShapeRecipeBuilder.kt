package recipe

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.ShapedRecipe

/**
 * @author karayuu
 */
class ShapeRecipeBuilder(name: String, item: ItemStack) : RecipeBuilder {
    private val key = NamespacedKey(AutoFarming.plugin, name)
    private val recipe = ShapedRecipe(key, item)

    /**
     * レシピの型を設定します.
     */
    fun shape(vararg shape: String): ShapeRecipeBuilder {
        recipe.shape(*shape)
        return this
    }

    /**
     * レシピの型を設定した文字の[Material]を設定します.
     */
    fun ingredient(letter: Char, material: Material): ShapeRecipeBuilder {
        recipe.setIngredient(letter, material)
        return this
    }

    /**
     * Recipeを返します.
     */
    override fun build(): Recipe = recipe
}