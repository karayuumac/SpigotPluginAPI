package component

/**
 * Builderを示すためのinterfaceです.
 * @author karayuu
 */
interface Builder<T> {
    fun build(): T
}