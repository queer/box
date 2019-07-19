package com.mewna.mc.box.listeners

import org.bukkit.Material
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.{EventHandler, Listener}

/**
 * @author amy
 * @since 7/9/19.
 */
class TrampleListener extends Listener {
  @EventHandler
  def onTrample(event: PlayerInteractEvent): Unit = {
    if(event.getAction == Action.PHYSICAL) {
      val block = event.getClickedBlock
      if(block != null && block.getType == Material.FARMLAND) {
        event.setCancelled(true)
      }
    }
  }
}
