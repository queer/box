package com.mewna.mc.box.listeners

import com.mewna.mc.box.Box
import com.mewna.mc.box.framework.di.Auto
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason

/**
 * @author amy
 * @since 7/9/19.
 */
class PhantomListener extends Listener {
  //noinspection VarCouldBeVal
  @Auto
  private var plugin: Box = _
  
  @EventHandler
  def onPhantomSpawn(event: CreatureSpawnEvent): Unit = {
    val entity = event.getEntity
    if(entity.getType == EntityType.PHANTOM && event.getSpawnReason == SpawnReason.NATURAL) {
      event.setCancelled(true)
      entity.setSilent(true)
      entity.setHealth(1)
      entity.setRemoveWhenFarAway(true)
      // This SHOULDN'T be an issue because I would have EXPECTED it to just pass a Runnable...
      // Whenever the Bukkit API updates to remove that method it should stop being an issue ig
      //noinspection ScalaDeprecation
      Bukkit.getScheduler.scheduleSyncDelayedTask(plugin, () => entity.remove(), 1)
    }
  }
}
