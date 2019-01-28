package data

import org.bukkit.entity.Player
import kotlin.Exception

/**
 * Created by karayuu on 2019/01/20
 */
class CannotFindPlayerException(player: Player) : Exception("[DataError] Can't find player[${player.name}]")

class CannotFindPlayerDataException(player: Player) : Exception("[DataError] Can't find ${player.name}'s data.")
