package com.mewna.mc.box.framework.command

import java.lang.reflect.Method

import com.mewna.mc.box.framework.BoxedPlugin
import com.mewna.mc.box.framework.command.annotation.{Default, Subcommand}
import com.mewna.mc.box.util.MessageUtil
import org.bukkit.ChatColor.RED
import org.bukkit.command.{Command, CommandSender}
import org.bukkit.entity.Player

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
  * @author amy
  * @since 7/11/19.
  */
class BoxedCommand(val src: Class[_],
                   val name: String,
                   val desc: String,
                   val usage: String,
                   val aliases: Array[String],
                   val permission: String,
                   val permissionMessage: String)
    extends Command(name, desc, usage, aliases.toList.asJava) {
  private val subcommands = mutable.Map[String, Method]()
  private var defaultSubcommand: Option[Method] = Option.empty

  def loadSubcommands(): BoxedCommand = {
    src.getDeclaredMethods
      .filter(e => e.isAnnotationPresent(classOf[Subcommand]))
      .foreach(m => {
        val subcommand = m.getDeclaredAnnotation(classOf[Subcommand])
        val names = subcommand.value()
        names.foreach(n => {
          if (subcommands.get(n).isDefined) {
            throw new IllegalStateException(
              f"Attempted to register subcommand '$n' for command class ${src.getName}, "
                + "but it's already been registered!"
            )
          }
          subcommands += (n -> m)
        })
        if (m.isAnnotationPresent(classOf[Default])) {
          if (defaultSubcommand.isEmpty) {
            defaultSubcommand = Option(m)
          } else {
            throw new IllegalStateException(
              f"Attempted to register default subcommand for command class ${src.getName}, "
                + "but it already has a default subcommand registered!"
            )
          }
        }
      })
    this
  }

  override def execute(commandSender: CommandSender,
                       cmdStr: String,
                       args: Array[String]): Boolean = {
    if (commandSender.hasPermission(permission) || commandSender.isOp) {
      val retVal = executeCommand(commandSender, cmdStr, args)
      if (!retVal) {
        MessageUtil.sendMessage(commandSender, RED + usage)
      }
    } else {
      MessageUtil.sendMessage(commandSender, RED + permissionMessage)
    }
    true
  }

  private def executeCommand(sender: CommandSender,
                             cmdStr: String,
                             args: Array[String]): Boolean = {
    var context = Map[Class[_ <: Any], Any]((classOf[CommandSender], sender))
    //noinspection TypeCheckCanBeMatch
    if (sender.isInstanceOf[Player]) {
      context += (classOf[Player] -> sender.asInstanceOf[Player])
    }
    val component = BoxedPlugin.locateCommandComponent(src, context)
    if (component.isDefined) {
      val instance = component.get
      if (args.isEmpty) {
        defaultSubcommand.get.invoke(instance, cmdStr, Array[String]())
      } else {
        val subcommandName = args.head
        val subcommandArgs = args.tail
        val maybeSubcommandMethod = subcommands.get(subcommandName)
        if (maybeSubcommandMethod.isDefined) {
          maybeSubcommandMethod.get.invoke(
            instance,
            subcommandName,
            subcommandArgs
          )
        } else {
          defaultSubcommand.get.invoke(instance, cmdStr, args)
        }
      }
    } else {
      MessageUtil.sendMessage(sender, RED + "Command not found.")
      throw new IllegalStateException(
        f"Asked to execute command of type ${src.getName}, " +
          "but somehow we don't have a component for it!?"
      )
    }
    true
  }
}
