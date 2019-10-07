package com.mewna.mc.box.gui

import java.util.UUID

import org.bukkit.entity.Player

import scala.collection.mutable

/**
 * @author amy
 * @since 7/10/19.
 */
object GuiManager {
  
  private val activeGuis = mutable.Map[UUID, GuiBase]()
  
  def getGui(player: Player): Option[GuiBase] = activeGuis.get(player.getUniqueId)
  def setGui(player: Player, gui: GuiBase): Unit = activeGuis.put(player.getUniqueId, gui)
  def isInGui(player: Player): Boolean = getGui(player).isDefined
  def clearGui(player: Player): Unit = activeGuis.remove(player.getUniqueId)
}
