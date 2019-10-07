package com.mewna.mc.box.components.mobstacking

import com.mewna.mc.box.framework.di.Auto
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.{EntityType, LivingEntity}
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.event.entity.EntityDropItemEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.{EquipmentSlot, ItemStack}
import org.bukkit.inventory.meta.{Damageable, ItemMeta}
import org.bukkit.Bukkit.getLogger

import scala.util.Random

/**
 * @author broman
 * @since 8/14/19.
 */
class MobHarvestListener extends Listener {
  //noinspection VarCouldBeVal
  @Auto
  private var component: ComponentMobStacking = _

  @EventHandler
  def onMobDrop(event: EntityDropItemEvent): Unit = {
    // Check if the mob is a stack and proportionally drop items as a stack
    val entity = event.getEntity
    //noinspection TypeCheckCanBeMatch
    if(entity.isInstanceOf[LivingEntity]) {
      val le = entity.asInstanceOf[LivingEntity]
      if(component.isStackable(entity)) {
        // Only interested in augmenting mob stack drops
        val item = event.getItemDrop
        val count = component.count(le)
        val itemStack = item.getItemStack
        // Drop items proportionally to mob stack count
        itemStack.setAmount(count * itemStack.getAmount)
        entity.getWorld.dropItem(item.getLocation, itemStack)
      }
    }
  }

  @EventHandler
  def onPlayerInteractWithEntity(event: PlayerInteractAtEntityEvent): Unit = {
    // Apply proportionate damage to shears when harvesting a sheep stack
    val entity = event.getRightClicked
    if(entity.getType == EntityType.SHEEP) {
      val inventory = event.getPlayer.getInventory
      val item = if (event.getHand == EquipmentSlot.HAND) {
        inventory.getItemInMainHand
      } else {
        inventory.getItemInOffHand
      }
      if(item.getType == Material.SHEARS) {
        val le = entity.asInstanceOf[LivingEntity]
        for(_ <- 2 to component.count(le)) {
          // We don't apply damage on the first shear since the game takes care of that.
          val newItemMeta: ItemMeta = applyDamage(item.asInstanceOf[ItemStack with Damageable], 1)
          item.setItemMeta(newItemMeta)
        }
      }
    }
  }

  def applyDamage[T <: ItemStack with Damageable](item: T, damage: Int): ItemMeta = {
    // Applies damage to a given ItemStack, factoring in the Unbreaking enchantment

    //noinspection ScalaStyle
    var itemAsDamageable: Damageable = null
    val unbreakingLevel = item.getEnchantmentLevel(Enchantment.DURABILITY)
    val r = new Random()
    // Formula taken from https://minecraft.gamepedia.com/Unbreaking
    val unbreakingCalculation = 100 / (unbreakingLevel + 1)
    if(r.nextInt(100) < unbreakingCalculation) {
      // If the unbreakingCalculation is higher than the random number, the "unbreaking check" failed
      itemAsDamageable = item.getItemMeta.asInstanceOf[Damageable]
      itemAsDamageable.setDamage(itemAsDamageable.getDamage + 1)
    }
    itemAsDamageable.asInstanceOf[ItemMeta]
  }
}
