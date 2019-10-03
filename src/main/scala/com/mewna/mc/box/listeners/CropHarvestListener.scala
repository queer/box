package com.mewna.mc.box.listeners

import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.block.data.Ageable
import org.bukkit.inventory.ItemStack
import com.massivecraft.factions.{Board, FLocation, FPlayers}

/**
  * @author broman
  * @since 8/19/19.
  */
class CropHarvestListener extends Listener {
  @EventHandler
  def onPlayerInteract(event: PlayerInteractEvent): Unit = {
    val block = event.getClickedBlock
    if (block == null) {
      return
    }
    val fPlayer = FPlayers.getInstance.getByPlayer(event.getPlayer)
    val faction = fPlayer.getFaction
    val location = block.getLocation
    val factionAtBlock = Board.getInstance.getFactionAt(new FLocation(location))
    if (faction.equals(factionAtBlock) || factionAtBlock.isWilderness) {
      // If the faction is player's faction, or the faction is the wild, we can replant
      val material = block.getType
      if (isCrop(material)) {
        block.getBlockData match {
          case blockAsAgeable: Ageable =>
            if (blockAsAgeable.getAge == blockAsAgeable.getMaximumAge) {
              // If the interacted block is a full crop, we harvest
              getApplicableDrops(material)
                .filter(_.getAmount != 0)
                .foreach(item => {
                  block.getWorld.dropItem(location, item)
                  block.setType(material)
                })
            }
          case _ =>
        }
      }
    }
  }

  def isCrop(item: Material): Boolean = {
    item match {
      case _ @(Material.WHEAT | Material.POTATOES | Material.CARROTS |
          Material.BEETROOTS) =>
        true
      case _ => false
    }
  }
  def getApplicableDrops(item: Material): Seq[ItemStack] = {
    val r = util.Random
    item match {
      case Material.WHEAT =>
        Seq(
          new ItemStack(Material.WHEAT),
          new ItemStack(Material.WHEAT_SEEDS, r.nextInt(4))
        )
      case Material.POTATOES =>
        Seq(new ItemStack(Material.POTATO, r.nextInt(4)))
      case Material.CARROTS =>
        Seq(new ItemStack(Material.CARROT, r.nextInt(5)))
      case Material.BEETROOTS =>
        Seq(
          new ItemStack(Material.BEETROOT),
          new ItemStack(Material.BEETROOT_SEEDS, r.nextInt(3))
        )
      case _ => Seq()
    }
  }
}
