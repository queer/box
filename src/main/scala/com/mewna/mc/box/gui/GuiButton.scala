package com.mewna.mc.box.gui

import org.bukkit.inventory.{Inventory, ItemStack}

/**
 * @author amy
 * @since 7/9/19.
 */
class GuiButton(val id: Int, val slotId: Int, val itemStack: ItemStack) {
  def addItem(inventory: Inventory): Unit = inventory.setItem(slotId, itemStack)
  
  def isButton(slotId: Int): Boolean = this.slotId == slotId
}
