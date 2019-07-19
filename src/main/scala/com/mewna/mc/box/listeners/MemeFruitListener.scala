package com.mewna.mc.box.listeners

import com.mewna.mc.box.util.MessageUtil
import org.bukkit.Location
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause
import org.bukkit.event.{EventHandler, Listener}

/**
 * Adapted from https://github.com/coty-crg/MemeFruitRaycastCheck/blob/master/src/com/wanderingcorgi/memefruitraycaster/PlayerListener.java
 *
 * @author amy
 * @since 7/12/19.
 */
class MemeFruitListener extends Listener {
  @EventHandler
  def onTeleport(event: PlayerTeleportEvent): Unit = {
    if(event.getCause == TeleportCause.CHORUS_FRUIT) {
      val worldStart = event.getFrom.getWorld
      val worldEnd = event.getTo.getWorld
      if(worldStart == worldEnd) {
        val from = event.getFrom.toVector
        val to = event.getTo.toVector
        val distance = from.distance(to)
        if(distance <= 32D) {
          // >=32 probably means it's not actually a memefruit
          var lastLocation = event.getFrom
          for(i <- 0 until distance.asInstanceOf[Int]) {
            val percent = i / distance
            val position = lerp(from, to, percent)
            val loc = new Location(worldStart, position.getBlockX, position.getBlockY, position.getBlockZ)
            val block = worldStart.getBlockAt(loc)
            val passable = block.isEmpty || block.isLiquid
            if(!passable && i > 0) {
              event.setTo(lastLocation)
              event.setCancelled(true)
              MessageUtil.sendMessage(event.getPlayer, f"Cannot meme through ${block.getType.name().toLowerCase()}.")
              return
            }
            lastLocation = loc
          }
        }
      }
    }
  }
  
  private def lerp(start: org.bukkit.util.Vector, end: org.bukkit.util.Vector, percent: Double): org.bukkit.util.Vector = {
    // We clone the vectors because apparently Bukkit vectors mutate in-place. wtf?
    val dir = end.clone().subtract(start)
    // Good thing we stopped caring about mutation after that one line :tada:
    // Scale the vector
    dir.multiply(percent).add(start)
  }
}
