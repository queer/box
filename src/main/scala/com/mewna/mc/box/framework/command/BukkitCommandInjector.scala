package com.mewna.mc.box.framework.command

import com.mewna.mc.box.BoxPlugin
import org.bukkit.command.CommandMap

/**
 * @author amy
 * @since 7/11/19.
 */
object BukkitCommandInjector {
  private[this] var instance: Option[BukkitCommandInjector[_ <: BoxPlugin]] = None
  
  def registerCommand[T <: BoxPlugin](plugin: T, command: org.bukkit.command.Command): Unit = {
    if(instance.isEmpty) {
      instance = Some(new BukkitCommandInjector(plugin))
    }
    instance.get.registerCommand(command)
  }
}

private class BukkitCommandInjector[T <: BoxPlugin](plugin: T) {
  var map: CommandMap = _
  
  def init(): Unit = {
    val field = plugin.getServer.getClass.getDeclaredField("commandMap")
    field.setAccessible(true)
    map = field.get(plugin.getServer).asInstanceOf[CommandMap]
  }
  
  def registerCommand(command: org.bukkit.command.Command): Unit = {
    if(map == null) {
      init()
    }
    map.register(command.getName, command.getLabel, command)
  }
}
