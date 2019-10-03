package com.mewna.mc.box.util

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

/**
  * @author amy
  * @since 7/9/19.
  */
object MessageUtil {
  val PREFIX: String = ""

  /**
    * Send a message to the given user, with the default prefix
    *
    * @param user    The user to message
    * @param message The message to send
    */
  def sendMessagePrefix(user: CommandSender, message: String): Unit = {
    sendMessage(user, PREFIX + message)
  }

  /**
    * Send a message to the given user, with the default prefix
    *
    * @param user     The user to message
    * @param dummy    Dummy argument to differentiate from { @link #sendMessagePrefix(CommandSender, String)}
    *                 because of the varargs
    * @param messages The messages to send
    */
  def sendMessagesPrefix(user: CommandSender,
                         dummy: Int,
                         messages: String*): Unit = {
    sendPrefixedMessages(user, PREFIX, messages.toSeq)
  }

  /**
    * Send a message to the given user
    *
    * @param user    The user to message
    * @param message The message to send
    */
  def sendMessage(user: CommandSender, message: String): Unit = {
    user.sendMessage(ChatColor.translateAlternateColorCodes('&', message))
  }

  /**
    * Send a message to the specified player with the given prefix
    *
    * @param user    The user to message
    * @param prefix  The prefix to use
    * @param message The message to send
    */
  def sendMessage(user: CommandSender,
                  prefix: String,
                  message: String): Unit = {
    sendMessage(user, prefix + ' ' + message)
  }

  /**
    * Send a series of messages to a user
    *
    * @param user     The user to message
    * @param messages The messages to send
    */
  def sendMessages(user: CommandSender, messages: Seq[String]): Unit = {
    user.sendMessage(
      messages
        .map(m => ChatColor.translateAlternateColorCodes('&', m))
        .toArray[String]()
    )
  }

  /**
    * Send a series of messages to the given user
    *
    * @param user     The user to message
    * @param prefix   The prefix to use
    * @param messages The messages to send
    */
  def sendPrefixedMessages(user: CommandSender,
                           prefix: String,
                           messages: Seq[String]): Unit = {
    sendMessages(user, messages.map((m => prefix + ChatColor.RESET + ' ' + m)))
  }
}
