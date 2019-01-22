package extension

/**
 * Created by karayuu on 2019/01/20
 */

/**
 * [to]になるまで待機します.
 */
fun <E> E.wait(to: E) {
    while (this != to) {

    }
}

/**
 * [from]から変更するまで待機します.
 */
fun <E> E.waitChnage(from: E) {
    while (this == from) {

    }
}
