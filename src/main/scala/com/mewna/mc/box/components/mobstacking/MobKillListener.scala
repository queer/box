package com.mewna.mc.box.components.mobstacking

import com.mewna.mc.box.framework.di.Auto
import org.bukkit.entity.LivingEntity
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.event.entity.EntityDeathEvent

/**
 * @author amy
 * @since 7/25/19.
 */
class MobKillListener extends Listener {
  //noinspection VarCouldBeVal
  @Auto
  private var component: ComponentMobStacking = _
  
  @EventHandler
  def onKill(event: EntityDeathEvent): Unit = {
    val entity = event.getEntity
    if(component.isStackable(entity)) {
      val count = component.count(entity)
      if(count > 1) {
        val next = count - 1
        val clone = entity.getWorld.spawnEntity(entity.getLocation, entity.getType).asInstanceOf[LivingEntity]
        if(next > 1) {
          // Update the stack's name if needed
          clone.setCustomName(f"${component.colour}$next")
          clone.setCustomNameVisible(true)
        } else {
          // Hide the name if there's only 1 entity left in the stack
          clone.setCustomName(null)
          clone.setCustomNameVisible(false)
        }
        // Make sure the new stack still has any aggro drawn by the previous stack
        if(event.getEntity.getKiller != null) {
          clone.damage(0, event.getEntity.getKiller)
        }
      } else {
        // Just hide the name anyway to be safe
        entity.setCustomNameVisible(false)
      }
    }
  }
}
