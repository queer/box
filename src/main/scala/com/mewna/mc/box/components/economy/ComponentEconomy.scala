package com.mewna.mc.box.components.economy

import com.mewna.mc.box.BoxPlugin
import com.mewna.mc.box.framework.BoxedPlugin
import com.mewna.mc.box.framework.component.{BoxedComponent, Component, Single}
import com.mewna.mc.box.framework.config.Config
import io.dekigokoro.client.DekigokoroClient
import net.milkbowl.vault.economy.Economy
import org.bukkit.plugin.ServicePriority

/**
 * @author amy
 * @since 7/10/19.
 */
@Single
@Component
class ComponentEconomy extends BoxedComponent {
  @Config("dekigokoro.api-key")
  private[this] var _key: String = _
  @Config("economy.name")
  private[this] var _economyName: String = _
  @Config("economy.currency.name.singular")
  private[this] var _currencyNameSingular: String = _
  @Config("economy.currency.name.plural")
  private[this] var _currencyNamePlural: String = _
  
  private var _dekigokoro: DekigokoroClient = _
  
  override def init(plugin: BoxPlugin): Boolean = {
    _dekigokoro = DekigokoroClient.create(_key)
    plugin.getServer.getServicesManager.register(classOf[Economy].asInstanceOf[Class[Object]],
      BoxedPlugin.locateComponent(classOf[DekigokoroEconomy]), plugin, ServicePriority.Highest)
    true
  }
  
  override def getName: String = "Economy"
  
  override def getDesc: String = "A dekigokoro.io-backed Minecraft server economy"
  
  def dekigokoro(): DekigokoroClient = _dekigokoro
  
  def economyName(): String = _economyName
  
  def currencyNameSingular(): String = _currencyNameSingular
  
  def currencyNamePlural(): String = _currencyNamePlural
}
