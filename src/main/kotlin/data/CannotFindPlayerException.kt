package data

import kotlin.Exception

/**
 * Created by karayuu on 2019/01/20
 */
class CannotFindPlayerException(override val message: String = "") : Exception()
