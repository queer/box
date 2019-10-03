package com.mewna.mc.box.tile

import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.event.block.SignChangeEvent

/**
  * @author amy
  * @since 7/9/19.
  */
abstract class CustomSign(val dominateLine: String) {
  def onSignEdited(event: SignChangeEvent): Unit

  def onSignClicked(player: Player, sign: Sign): Unit

  def verifyDominateLine(s: String): Boolean = dominateLine.equalsIgnoreCase(s)

  def onSignPunched(player: Player, sign: Sign): Unit = {}
}
