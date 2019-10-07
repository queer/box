package com.mewna.mc.box.util

import org.bukkit.ChatColor
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BlockStateMeta

/**
 * Adapted from
 * - https://github.com/EssentialsX/Essentials/blob/2.x/nms/NMSProvider/src/net/ess3/nms/SpawnerProvider.java
 * - https://github.com/EssentialsX/Essentials/blob/2.x/nms/UpdatedMetaProvider/src/net/ess3/nms/updatedmeta/BlockMetaSpawnerProvider.java
 *
 * @author amy
 * @since 7/9/19.
 */
object SpawnerUtil {
  private val ENTITY_TO_DISPLAY_NAME: Map[EntityType, String] = Map(
    (EntityType.CAVE_SPIDER, "Cave Spider"),
    (EntityType.PIG_ZOMBIE, "Zombie Pigman"),
    (EntityType.MAGMA_CUBE, "Magma Cube"),
    (EntityType.ENDER_DRAGON, "Ender Dragon"),
    (EntityType.MUSHROOM_COW, "Mooshroom"),
    (EntityType.SNOWMAN, "Snow Golem"),
    (EntityType.OCELOT, "Ocelot"),
    (EntityType.IRON_GOLEM, "Iron Golem"),
    (EntityType.WITHER, "Wither"),
    (EntityType.HORSE, "Horse")
  )
  
  def setEntityType(is: ItemStack, `type`: EntityType): ItemStack = {
    
    val bsm = is.getItemMeta.asInstanceOf[BlockStateMeta]
    val bs = bsm.getBlockState
    bs.asInstanceOf[CreatureSpawner].setSpawnedType(`type`)
    bsm.setBlockState(bs)
    is.setItemMeta(bsm)
    setDisplayName(is, `type`)
  }
  
  def getEntityType(is: ItemStack): EntityType = {
    val bsm = is.getItemMeta.asInstanceOf[BlockStateMeta]
    val bs = bsm.getBlockState.asInstanceOf[CreatureSpawner]
    bs.getSpawnedType
  }
  
  private def setDisplayName(is: ItemStack, `type`: EntityType) = {
    val meta = is.getItemMeta
    // TODO: How to resolve this deprecation warning?
    val displayName = ENTITY_TO_DISPLAY_NAME.getOrElse(`type`, `type`.getName)
    meta.setDisplayName(ChatColor.RESET + displayName + " Spawner")
    is.setItemMeta(meta)
    is
  }
}
