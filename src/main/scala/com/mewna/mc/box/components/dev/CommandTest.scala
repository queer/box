package com.mewna.mc.box.components.dev

import com.mewna.mc.box.framework.command.annotation.{Command, Default, Subcommand}
import com.mewna.mc.box.framework.di.Auto
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * @author amy
 * @since 7/11/19.
 */
@Command(name = "test", permissionNode = "box.test")
class CommandTest {
  @Auto
  private var sender: CommandSender = _
  @Auto
  private var player: Player = _
  
  @Default
  @Subcommand(Array("test-sender"))
  def testSender(commandString: String, args: Array[String]): Unit = {
    sender.sendMessage(ChatColor.GREEN + f"Sender is player: ${player != null}")
  }
  
  @Subcommand(Array("something"))
  def something(commandString: String, args: Array[String]): Unit = {
    sender.sendMessage(ChatColor.GOLD + "Something.")
  }
}
