package com.mewna.mc.box.components.shop

import com.mewna.mc.box.Box
import com.mewna.mc.box.components.economy.DekigokoroEconomy
import com.mewna.mc.box.framework.BoxedPlugin
import com.mewna.mc.box.gui.{GuiBase, GuiButton}
import com.mewna.mc.box.util.MessageUtil
import net.milkbowl.vault.economy.EconomyResponse.ResponseType
import org.bukkit.entity.Player
import org.bukkit.inventory.{Inventory, ItemStack}
import org.bukkit.{ChatColor, Material}

import scala.util.control.Breaks._

/**
 * @author amy
 * @since 7/9/19.
 */
class ShopGui(override val plugin: Box, val shop: ComponentShop, val itemName: String)
  extends GuiBase(plugin, shop.shopGuiName, GuiBase.ROWS_4) {
  private var shopItem: Option[ShopItem] = _
  private val economy = BoxedPlugin.locateComponent(classOf[DekigokoroEconomy]).get
  
  //noinspection ScalaStyle
  override protected def addButtons(): Unit = {
    try {
      shopItem = shop.resolveItem(itemName)
    } catch {
      case e: Exception =>
        shopItem = None
        addButton(0, 13, getWithName(Material.BARRIER, ChatColor.RED + "Error loading shop! Please report this!"))
        e.printStackTrace()
        return
    }
    if(shopItem.isEmpty) {
      addButton(0, 13, getWithName(Material.BARRIER, ChatColor.RED + "Item not found! Please report this!"))
    } else {
      val item = shopItem.get
      val shopStack = item.stack
      // Add buy stacks
      for(i <- 0 until 7) {
        val stack = getWithName(shopStack.getType, (ChatColor.WHITE + "Buy - $" + ChatColor.GREEN + "%.2f")
          .format(item.buyPrice * Math.pow(2, i)))
        stack.setData(shopStack.getData.clone())
        stack.setAmount(Math.pow(2, i).toInt)
        addButton(i, 10 + i, stack)
      }
      // Add sell stacks
      for(i <- 0 until 7) {
        val stack = getWithName(shopStack.getType, (ChatColor.WHITE + "Sell - $" + ChatColor.RED + "%.2f")
          .format(item.sellPrice * Math.pow(2, i)))
        stack.setData(shopStack.getData.clone())
        stack.setAmount(Math.pow(2, i).toInt)
        addButton(7 + i, 19 + i, stack)
      }
      var fake = 100
      addButton(fake, 9, new ItemStack(Material.GREEN_STAINED_GLASS_PANE))
      fake += 1
      addButton(fake, 17, new ItemStack(Material.GREEN_STAINED_GLASS_PANE))
      fake += 1
      addButton(fake, 18, new ItemStack(Material.RED_STAINED_GLASS_PANE))
      fake += 1
      addButton(fake, 26, new ItemStack(Material.RED_STAINED_GLASS_PANE))
      fake += 1
      for(i <- 0 to 8) {
        addButton(fake, i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE))
        fake += 1
      }
      for(i <- 0 to 8) {
        addButton(fake, 27 + i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE))
        fake += 1
      }
    }
  }
  
  override protected def handleButtonClick(player: Player, button: GuiButton): Unit = {
    if(button.id < 7) {
      val size = Math.pow(2, button.id).toInt
      val price = shopItem.get.buyPrice
      val balance = economy.getBalance(player)
      if(balance >= price) {
        val economyResponse = economy.withdrawPlayer(player, price)
        
        economyResponse.`type` match {
          case ResponseType.NOT_IMPLEMENTED | ResponseType.FAILURE =>
            // TODO: Configurable message...
            MessageUtil.sendMessage(player, ChatColor.RED + "Couldn't withdraw money!")
            return
          case ResponseType.SUCCESS =>
        }
        
        val stack = shopItem.get.stack.clone
        stack.setAmount(size)
        player.getInventory.addItem(stack)
        MessageUtil.sendMessage(player, shop.buySuccessMessage
          .replace("%number%", "" + size)
          .replace("%item%", shopItem.get.name)
          .replace("%cost%", "" + price))
      } else {
        MessageUtil.sendMessage(player, shop.buyFailMessage
          .replace("%number%", "" + size)
          .replace("%item%", shopItem.get.name))
      }
    } else if(button.id < 14) {
      val size = Math.pow(2, button.id - 7).toInt
      val price = shopItem.get.sellPrice * size
      val count = countMatInInv(player, shopItem.get.stack.getType)
      if(count < size) {
        MessageUtil.sendMessage(player, shop.sellFailMessage
          .replace("%item%", shopItem.get.name))
      } else {
        removeItem(player.getInventory, shopItem.get.stack.getType, size)
        val response = /*plugin.*/economy.depositPlayer(player, price)
        response.`type` match {
          case ResponseType.NOT_IMPLEMENTED | ResponseType.FAILURE =>
            // TODO: Configurable message...
            MessageUtil.sendMessage(player, ChatColor.RED + "Couldn't deposit money!")
            return
          case ResponseType.SUCCESS =>
        }
        MessageUtil.sendMessage(player, shop.sellSuccessMessage
          .replace("%number%", "" + size)
          .replace("%item%", shopItem.get.name)
          .replace("%cost%", "" + price))
      }
    }
  }
  
  private def countMatInInv(p: Player, mat: Material) = {
    var count = 0
    for(
      stack <- p.getInventory.getContents
      if stack != null
      if stack.getType == mat
      if !stack.hasItemMeta
    ) {
      count += stack.getAmount
    }
    count
  }
  
  private def removeItem(inventory: Inventory, mat: Material, quantity: Int) = {
    var rest = quantity
    
    for(i <- inventory.getContents.indices) {
      val stack = inventory.getItem(i)
      if(stack != null && stack.getType == mat && !stack.hasItemMeta) {
        if(rest >= stack.getAmount) {
          rest -= stack.getAmount
          inventory.clear(i)
        } else if(rest > 0) {
          stack.setAmount(stack.getAmount - rest)
          rest = 0
        } else {
          break
        }
      }
    }
    quantity - rest
  }
}
