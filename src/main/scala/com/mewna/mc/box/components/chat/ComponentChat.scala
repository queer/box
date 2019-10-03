package com.mewna.mc.box.components.chat

import com.mewna.catnip.Catnip
import com.mewna.catnip.entity.message.Message
import com.mewna.catnip.shard.DiscordEvent
import com.mewna.mc.box.BoxPlugin
import com.mewna.mc.box.framework.component.BoxedComponent
import com.mewna.mc.box.framework.config.Config
import org.bukkit.Bukkit

//noinspection VarCouldBeVal
class ComponentChat extends BoxedComponent {

  @Config("discord.api-key")
  private var token: String = _
  @Config("discord.channel-id")
  private var channel: String = _

  override def init(plugin: BoxPlugin): Boolean = {
    val catnip = Catnip.catnip(token)
    catnip.on(DiscordEvent.MESSAGE_CREATE, (msg: Message) => {
      if (msg.idAsLong.equals(channel.asInstanceOf[Long])) {
        Bukkit.broadcastMessage(msg.content)
      }
      ()
    })
    catnip.connect()
    true
  }

  override def getName: String = "Courier"

  override def getDesc: String = "A simple Discord <-> Minecraft chat relay"
}
