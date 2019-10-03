package com.mewna.mc.box.components.shop

import com.mewna.mc.box.Box
import com.mewna.mc.box.framework.component.Component
import com.mewna.mc.box.framework.di.Auto
import com.mewna.mc.box.gui.GuiManager
import com.mewna.mc.box.tile.CustomSign
import org.bukkit.ChatColor
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.event.block.SignChangeEvent

/**
  * @author amy
  * @since 7/9/19.
  */
@Component
class ShopSign extends CustomSign(ChatColor.RED + "[Shop]") {
  //noinspection VarCouldBeVal
  @Auto
  private var plugin: Box = _
  //noinspection VarCouldBeVal
  @Auto
  private var shop: ComponentShop = _

  override def onSignEdited(event: SignChangeEvent): Unit = {
    if (event.getPlayer.hasPermission("box.shop.create")) {
      val line = ChatColor.translateAlternateColorCodes('&', event.getLine(0))
      if (verifyDominateLine(line)) {
        event.setLine(0, dominateLine)

        val maybe: Option[ShopItem] =
          shop.resolveItem(event.getLine(1).replaceAll("\\s+", ""))
        if (maybe.isEmpty) {
          event.setLine(
            0,
            ChatColor.DARK_RED + ChatColor.stripColor(dominateLine)
          )
          event.setLine(1, ChatColor.DARK_RED + "Bad item name!")
        }
      }
    }
  }

  override def onSignClicked(player: Player, sign: Sign): Unit = {
    if (verifyDominateLine(sign.getLine(0))) {
      val gui = new ShopGui(plugin, shop, sign.getLine(1)).initialize()
      GuiManager.setGui(player, gui)
      gui.displayGui(player)
    }
  }
}
