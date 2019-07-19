package com.mewna.mc.box.listeners

import com.mewna.mc.box.framework.BoxedPlugin
import com.mewna.mc.box.gui.GuiManager
import com.mewna.mc.box.tile.SignRegistry
import org.bukkit.Material
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.event.block.{Action, SignChangeEvent}
import org.bukkit.event.inventory.{InventoryClickEvent, InventoryCloseEvent}
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.{EventHandler, Listener}

/**
 * @author amy
 * @since 7/10/19.
 */
class PlayerListener extends Listener {
  @EventHandler
  def onGuiClick(event: InventoryClickEvent): Unit = {
    event.getWhoClicked match {
      case p: Player =>
        if(GuiManager.isInGui(p)) {
          event.setCancelled(true)
          GuiManager.getGui(p).get.onButtonPressed(p, event.getSlot)
        }
      case _ =>
    }
  }
  
  @EventHandler
  def onGuiClose(event: InventoryCloseEvent): Unit = {
    val player = event.getPlayer.asInstanceOf[Player]
    if(GuiManager.isInGui(player)) {
      GuiManager.getGui(player).get.onGuiClosed()
      GuiManager.clearGui(player)
    }
  }
  
  @EventHandler
  def onSignChange(event: SignChangeEvent): Unit = {
    BoxedPlugin.locateComponent(classOf[SignRegistry])
      .get.customSigns.foreach(_.onSignEdited(event))
  }
  
  @EventHandler
  def onPlayerInteract(event: PlayerInteractEvent): Unit = {
    val block = event.getClickedBlock
    if(block != null && (block.getType == Material.WALL_SIGN || block.getType == Material.SIGN)) {
      val sign = block.getState.asInstanceOf[Sign]
      // Note: We compare INCLUDING color codes here for a reason
      // This way, players can't make their own signs and bypass it.
      val topLine = sign.getLines()(0)
      
      if(event.getAction eq Action.RIGHT_CLICK_BLOCK) {
        BoxedPlugin.locateComponent(classOf[SignRegistry])
          .get.customSigns
          .filter(_.verifyDominateLine(topLine))
          .foreach(_.onSignClicked(event.getPlayer, sign))
      } else if(event.getAction eq Action.LEFT_CLICK_BLOCK) {
        BoxedPlugin.locateComponent(classOf[SignRegistry])
          .get.customSigns
          .filter(_.verifyDominateLine(topLine))
          .foreach(_.onSignPunched(event.getPlayer, sign))
      }
    }
  }
}
