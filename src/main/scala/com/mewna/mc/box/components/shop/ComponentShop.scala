package com.mewna.mc.box.components.shop

import com.mewna.mc.box.BoxPlugin
import com.mewna.mc.box.framework.BoxedPlugin
import com.mewna.mc.box.framework.component.{BoxedComponent, Component, Single}
import com.mewna.mc.box.framework.config.Config
import com.mewna.mc.box.tile.SignRegistry
import com.mewna.mc.box.util.ItemUtil
import org.bukkit.inventory.ItemStack

import scala.collection.mutable

/**
 * @author amy
 * @since 7/9/19.
 */
@Single
@Component
class ComponentShop extends BoxedComponent {
  //noinspection VarCouldBeVal
  @Config("shop.messages.buy-success")
  private[this] var _buySuccessMessage: String = _
  //noinspection VarCouldBeVal
  @Config("shop.messages.buy-fail")
  private[this] var _buyFailMessage: String = _
  //noinspection VarCouldBeVal
  @Config("shop.messages.sell-success")
  private[this] var _sellSuccessMessage: String = _
  //noinspection VarCouldBeVal
  @Config("shop.messages.sell-fail")
  private[this] var _sellFailMessage: String = _
  //noinspection VarCouldBeVal
  @Config("shop.gui.name")
  private[this] var _shopGuiName: String = _
  val shopitemMap: mutable.Map[String, ShopItem] = mutable.Map[String, ShopItem]()
  
  override def loadConfig(plugin: BoxPlugin): Unit = {
    // Load shop items
    plugin.getConfig.getConfigurationSection("shop.prices").getKeys(false).forEach(e => {
      val name = plugin.getConfig.getString("shop.prices." + e + ".name")
      val buyPrice = plugin.getConfig.getDouble("shop.prices." + e + ".buy")
      val sellPrice = plugin.getConfig.getDouble("shop.prices." + e + ".sell")
      var stack: Option[ItemStack] = None
      val mapKey = name.toLowerCase.replaceAll("\\s+", "")
      try
        stack = Some(ItemUtil.get(mapKey))
      catch {
        case _: Exception =>
          try
            stack = Some(ItemUtil.get(e))
          catch {
            case e2: Exception =>
              plugin.getLogger.warning("Couldn't load shopitem at key '" + e + "' with name '" + name + "'!")
              e2.printStackTrace()
              return
          }
      }
      shopitemMap.put(mapKey, ShopItem(e, name, stack.get, buyPrice, sellPrice))
    })
    plugin.getLogger.info("[Shop] Loaded " + shopitemMap.size + " shop items!")
  }
  
  override def init(plugin: BoxPlugin): Boolean = {
    val signComponent = BoxedPlugin.locateComponent(classOf[ShopSign]).get
    BoxedPlugin.locateComponent(classOf[SignRegistry])
      .foreach(registry => registry.customSigns += signComponent)
    true
  }
  
  override def getName: String = "Shop"
  
  override def getDesc: String = "A magical sign-shop for special people~"
  
  def buySuccessMessage: String = _buySuccessMessage
  
  def buyFailMessage: String = _buyFailMessage
  
  def sellSuccessMessage: String = _sellSuccessMessage
  
  def sellFailMessage: String = _sellFailMessage
  
  def shopGuiName: String = _shopGuiName
  
  def resolveItem(name: String): Option[ShopItem] = {
    val cleaned = name.toLowerCase.replaceAll("\\s+", "")
    val item = shopitemMap.get(cleaned)
    if(item.isEmpty) {
      shopitemMap.find(_._1.equalsIgnoreCase(cleaned)).map(_._2)
    } else {
      item
    }
  }
}
