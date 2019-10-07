package com.mewna.mc.box.gui

import com.mewna.mc.box.Box
import org.bukkit.{Bukkit, ChatColor, Material}
import org.bukkit.entity.Player
import org.bukkit.inventory.{Inventory, ItemStack}

import scala.collection.mutable
import scala.collection.JavaConverters._

object GuiBase {
  val ROWS_1 = 9
  val ROWS_2 = 18
  val ROWS_3 = 27
  val ROWS_4 = 36
  val ROWS_5 = 45
  val ROWS_6 = 54
}

/**
 * @author amy
 * @since 7/9/19.
 */
abstract class GuiBase(val plugin: Box, val title: String, val slots: Int) {
  val buttons: mutable.Map[Int, GuiButton] = mutable.Map()
  
  private var _inventory: Inventory = _
  
  def initialize[T >: GuiBase](): T = {
    addButtons()
    //noinspection ScalaStyle
    _inventory = Bukkit.createInventory(null, slots, title)
    this
  }
  
  protected def addButtons(): Unit
  
  protected def handleButtonClick(player: Player, button: GuiButton): Unit
  
  def displayGui(p: Player): Unit = {
    for((_, b) <- buttons) {
      b.addItem(_inventory)
    }
    p.openInventory(_inventory)
  }
  
  def onGuiClosed(): Unit = {
  }
  
  def inventory(): Inventory = _inventory
  
  protected final def addButton(id: Int, i: ItemStack): Unit = {
    val data = (buttons.size, new GuiButton(id, buttons.size, i))
    buttons += data
  }
  
  protected final def addButton(id: Int, slot: Int, i: ItemStack): Unit = {
    val data = (slot, new GuiButton(id, slot, i))
    buttons += data
  }
  
  final def onButtonPressed(p: Player, slot: Int): Unit = {
    handleButtonClick(p, buttons(slot))
  }
  
  protected final def getWithName(m: Material, name: String): ItemStack = {
    val stack = new ItemStack(m)
    val meta = stack.getItemMeta
    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name))
    stack.setItemMeta(meta)
    stack
  }
  
  protected final def getWithNameAndLore(m: Material, name: String, lore: Seq[String]): ItemStack = {
    val stack = new ItemStack(m)
    val meta = stack.getItemMeta
    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name))
    meta.setLore(lore.map(s => ChatColor.translateAlternateColorCodes('&', s)).toList.asJava)
    stack.setItemMeta(meta)
    stack
  }
}
