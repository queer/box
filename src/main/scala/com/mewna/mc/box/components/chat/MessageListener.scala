package com.mewna.mc.box.components.chat

import com.mewna.catnip.Catnip
import com.mewna.mc.box.framework.config.Config
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

//noinspection VarCouldBeVal
class MessageListener extends Listener {
  @Config("discord.api-key")
  private var token: String = _

  @Config("discord.channel-id")
  private var channel: String = _

  private val catnip = Catnip.catnip(token)

  def onMinecraftMessage(event: AsyncPlayerChatEvent): Unit = {
    catnip.rest().channel().sendMessage(channel, event.getMessage)
  }
}
