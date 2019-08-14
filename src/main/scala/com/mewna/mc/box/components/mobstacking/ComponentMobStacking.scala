package com.mewna.mc.box.components.mobstacking

import com.mewna.mc.box.BoxPlugin
import com.mewna.mc.box.framework.component.{BoxedComponent, Component, Single}
import org.bukkit.entity._
import org.bukkit.material.Colorable
import org.bukkit.{ChatColor, World}

import scala.collection.JavaConverters._

/**
 * @author amy
 * @since 7/25/19.
 */
@Single
@Component
class ComponentMobStacking extends BoxedComponent {
  private[this] var task: Int = _
  
  val maxCount = 8
  val radius = 5D
  val colour = ChatColor.RED
  
  override def init(plugin: BoxPlugin): Boolean = {
    //noinspection ScalaDeprecation,ScalaStyle
    task = plugin.getServer.getScheduler.scheduleSyncRepeatingTask(plugin, () => {
      plugin.getServer.getWorlds.forEach(this.process)
    }, 20L, 20L)
    true
  }
  
  def isStackable(entity: Entity): Boolean = {
    !(
      // Slimes would become unkillable gods. Also a massive slime exploit.
      entity.isInstanceOf[Slime]
        // Magma cubes are just like slimes.
        || entity.isInstanceOf[MagmaCube]
        // Dude, I don't even wanna imagine what this would be like...
        || entity.isInstanceOf[Enderman]
        // Don't stack tameable entities
        || entity.isInstanceOf[Tameable]
        // Don't stack entities with inventories
        || entity.isInstanceOf[InventoryHolder]
      )
  }
  
  def count(entity: LivingEntity): Int = {
    var entityCount = 1
    val customName = entity.getCustomName
    if(customName != null && customName.startsWith(colour.toString)) try
      entityCount = ChatColor.stripColor(customName).toInt
    catch {
      case _: NumberFormatException =>
      
    }
    entityCount
  }
  
  //noinspection ScalaStyle
  private def process(world: World): Unit = {
    for(entity <- world.getEntities.asScala) {
      // Don't re-stack things like items, exp, etc.
      // We don't use a match here because that just seems like an ugly waste
      // of code.
      //noinspection TypeCheckCanBeMatch
      if(entity.isInstanceOf[LivingEntity]) {
        if(entity.isValid && isStackable(entity)) {
          val original = count(entity.asInstanceOf[LivingEntity])
          var removed = 0
          entity.getNearbyEntities(radius, radius, radius).asScala
            .filter(_.isInstanceOf[LivingEntity])
            .map(_.asInstanceOf[LivingEntity])
            .filter(_.isValid)
            .filter(_.getType != EntityType.PLAYER)
            .filter(check(entity, _))
            .foreach(other => {
              val otherCount = count(other)
              if(original + removed + otherCount <= maxCount) {
                other.remove()
                removed += otherCount
              }
            })
          if(removed > 0) {
            // Set the entity's name so we can parse out the stack size later
            entity.setCustomNameVisible(true)
            entity.setCustomName(f"$colour${Integer.toString(original + removed)}")
          }
        }
      }
    }
  }
  
  private def check(a: Entity, b: Entity): Boolean = {
    // Merge if they're the same type
    (a.getType == b.getType) &&
      // If they're ageable (ex. animals, etc.), merge if they're the same age
      // That is, don't convert baby animals into giant stacks
      (!a.isInstanceOf[Ageable]
        || !b.isInstanceOf[Ageable]
        || a.asInstanceOf[Ageable].isAdult == b.asInstanceOf[Ageable].isAdult) &&
      // Don't make our rainbow sheep poof either
      (!a.isInstanceOf[Colorable]
        || !b.isInstanceOf[Colorable]
        || (a.asInstanceOf[Colorable].getColor == b.asInstanceOf[Colorable].getColor))
  }
  
  override def getName: String = "Mob Stacking"
  
  override def getDesc: String = "Stack up mobs to help alleviate lag"
}
