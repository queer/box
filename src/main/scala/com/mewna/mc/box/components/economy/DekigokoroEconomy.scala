package com.mewna.mc.box.components.economy

import java.util

import com.mewna.mc.box.framework.component.{Component, Single}
import com.mewna.mc.box.framework.di.Auto
import net.milkbowl.vault.economy.EconomyResponse.ResponseType
import net.milkbowl.vault.economy.{Economy, EconomyResponse}
import org.bukkit.OfflinePlayer

import scala.compat.java8.OptionConverters._

/**
 * @author amy
 * @since 7/10/19.
 */
@Single
@Component
class DekigokoroEconomy extends Economy {
  //noinspection VarCouldBeVal
  @Auto
  private var econ: ComponentEconomy = _
  private val NO_BANK_SUPPORT = new EconomyResponse(0, 0,
    ResponseType.NOT_IMPLEMENTED,
    "Banks are not supported by the current dekigokoro economy implementation!")
  
  override def isEnabled: Boolean = {
    true
  }
  
  override def getName: String = econ.economyName()
  
  override def hasBankSupport: Boolean = false
  
  override def fractionalDigits(): Int = -1
  
  override def format(amount: Double): String = "%.2f".format(amount)
  
  override def currencyNamePlural(): String = econ.currencyNamePlural()
  
  override def currencyNameSingular(): String = econ.currencyNameSingular()
  
  override def hasAccount(playerName: String): Boolean = throw new UnsupportedOperationException("Use UUIDs!")
  
  override def hasAccount(player: OfflinePlayer): Boolean = true
  
  override def hasAccount(playerName: String, worldName: String): Boolean = throw new UnsupportedOperationException("Use UUIDs!")
  
  override def hasAccount(player: OfflinePlayer, worldName: String): Boolean = true
  
  override def getBalance(playerName: String): Double = throw new UnsupportedOperationException("Use UUIDs!")
  
  override def getBalance(player: OfflinePlayer): Double = getBalance(player, "")
  
  override def getBalance(playerName: String, world: String): Double = throw new UnsupportedOperationException("Use UUIDs!")
  
  override def getBalance(player: OfflinePlayer, world: String): Double = {
    val value = econ.dekigokoro().getCurrencyHandler.getBalance(player.getUniqueId.toString).join().asScala
    value.map(c => c.getBalance.doubleValue()).getOrElse(0D)
  }
  
  override def has(playerName: String, amount: Double): Boolean = throw new UnsupportedOperationException("Use UUIDs!")
  
  override def has(player: OfflinePlayer, amount: Double): Boolean = has(player, "", amount)
  
  override def has(playerName: String, worldName: String, amount: Double): Boolean = throw new UnsupportedOperationException("Use UUIDs!")
  
  override def has(player: OfflinePlayer, worldName: String, amount: Double): Boolean = {
    val res = econ.dekigokoro().getCurrencyHandler.getBalance(player.getUniqueId.toString).join().asScala
    res.map(c => c.getBalance.doubleValue()).getOrElse(0D) >= amount
  }
  
  override def withdrawPlayer(playerName: String, amount: Double): EconomyResponse = throw new UnsupportedOperationException("Use UUIDs!")
  
  override def withdrawPlayer(player: OfflinePlayer, amount: Double): EconomyResponse = withdrawPlayer(player, "", amount)
  
  override def withdrawPlayer(playerName: String, worldName: String, amount: Double): EconomyResponse = throw new UnsupportedOperationException("Use UUIDs!")
  
  override def withdrawPlayer(player: OfflinePlayer, worldName: String, amount: Double): EconomyResponse = {
    val res = econ.dekigokoro().getCurrencyHandler
      .incrementBalance(player.getUniqueId.toString, BigDecimal(-amount).bigDecimal).join().asScala
    if(res.isDefined) {
      val balance = getBalance(player, worldName)
      new EconomyResponse(amount, balance, ResponseType.SUCCESS, "")
    } else {
      new EconomyResponse(0, 0, ResponseType.FAILURE, "No such player!")
    }
  }
  
  override def depositPlayer(playerName: String, amount: Double): EconomyResponse = throw new UnsupportedOperationException("Use UUIDs!")
  
  override def depositPlayer(player: OfflinePlayer, amount: Double): EconomyResponse = depositPlayer(player, "", amount)
  
  override def depositPlayer(playerName: String, worldName: String, amount: Double): EconomyResponse = throw new UnsupportedOperationException("Use UUIDs!")
  
  override def depositPlayer(player: OfflinePlayer, worldName: String, amount: Double): EconomyResponse = {
    val res = econ.dekigokoro().getCurrencyHandler
      .incrementBalance(player.getUniqueId.toString, BigDecimal(amount).bigDecimal).join().asScala
    if(res.isDefined) {
      val balance = getBalance(player, worldName)
      new EconomyResponse(amount, balance, ResponseType.SUCCESS, "")
    } else {
      new EconomyResponse(0, 0, ResponseType.FAILURE, "No such player!")
    }
  }
  
  override def createBank(name: String, player: String): EconomyResponse = NO_BANK_SUPPORT
  
  override def createBank(name: String, player: OfflinePlayer): EconomyResponse = NO_BANK_SUPPORT
  
  override def deleteBank(name: String): EconomyResponse = NO_BANK_SUPPORT
  
  override def bankBalance(name: String): EconomyResponse = NO_BANK_SUPPORT
  
  override def bankHas(name: String, amount: Double): EconomyResponse = NO_BANK_SUPPORT
  
  override def bankWithdraw(name: String, amount: Double): EconomyResponse = NO_BANK_SUPPORT
  
  override def bankDeposit(name: String, amount: Double): EconomyResponse = NO_BANK_SUPPORT
  
  override def isBankOwner(name: String, playerName: String): EconomyResponse = NO_BANK_SUPPORT
  
  override def isBankOwner(name: String, player: OfflinePlayer): EconomyResponse = NO_BANK_SUPPORT
  
  override def isBankMember(name: String, playerName: String): EconomyResponse = NO_BANK_SUPPORT
  
  override def isBankMember(name: String, player: OfflinePlayer): EconomyResponse = NO_BANK_SUPPORT
  
  override def getBanks: util.List[String] = new util.ArrayList[String]()
  
  override def createPlayerAccount(playerName: String): Boolean = throw new UnsupportedOperationException("Use UUIDs!")
  
  override def createPlayerAccount(player: OfflinePlayer): Boolean = true
  
  override def createPlayerAccount(playerName: String, worldName: String): Boolean = throw new UnsupportedOperationException("Use UUIDs!")
  
  override def createPlayerAccount(player: OfflinePlayer, worldName: String): Boolean = true
}
