package com.mewna.mc.box.util

import org.bukkit.entity.EntityType
import org.bukkit.{Material, Statistic}

import scala.util.Try

/**
 * Adapted from https://github.com/EssentialsX/Essentials/blob/2.x/Essentials/src/com/earth2me/essentials/utils/EnumUtil.java
 *
 * @author amy
 * @since 7/9/19.
 */
object EnumUtil {
  def valueOf[T <: Enum[T]](clazz: Class[T], names: Seq[String]): Option[T] =
    names.find { name =>
      Try(clazz.getDeclaredField(name)).isSuccess
    }.map(Enum.valueOf(clazz, _))

  def getAllMatching[T <: Enum[T]](clazz: Class[T], names: Seq[String]): Set[T] =
    clazz.getEnumConstants
      .filter(constant => names.contains(constant.name()))
      .toSet
  
  def getMaterial(names: Seq[String]): Option[Material] = valueOf(classOf[Material], names)
  
  def getStatistic(names: Seq[String]): Option[Statistic] = valueOf(classOf[Statistic], names)
  
  def getEntityType(names: Seq[String]): Option[EntityType] = valueOf(classOf[EntityType], names)
}
