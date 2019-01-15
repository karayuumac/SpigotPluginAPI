package menu.component

import org.bukkit.Material

/**
 * Created by karayuu on 2019/01/04
 */

/**
 * Menu作成時に使われるスロットの概念
 */
interface Slot {
    /**
     * メニューに表示すべきアイコンを返します
     */
    fun icon(): Material
}

/**
 * シンプルなボタンのクラスです
 */
abstract class Button : Slot {
    /**
     * [icon] で指定した [Material] をメニュー外に取り出せるかを返します
     */
    abstract fun canPick(): Boolean
}

/**
 * 押したら何かしらのアクションを起こすボタンのクラスです
 */
abstract class FunctionalButton : Button() {
    /**
     * このボタンを押した際のアクションを指定します。
     */
    abstract fun function()
}
