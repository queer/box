package com.mewna.mc.box.components.chat

import com.mewna.catnip.Catnip
import com.mewna.catnip.entity.message.Message
import com.mewna.catnip.shard.DiscordEvent

import com.mewna.catnip.Catnip
import com.mewna.mc.box.framework.config.Config

class CourierChat {
  //noinspection VarCouldBeVal
  @Config("discord.bot-token")
  private[this] var _token: String = _

  val client: Catnip = Catnip.catnip(_token)

  client.on(DiscordEvent.MESSAGE_CREATE, (msg: Message) => {
    if (msg.content().equals("!ping")) {
      msg.channel().sendMessage("pong!")
    }
    ()
  })
  client.connect()
}
