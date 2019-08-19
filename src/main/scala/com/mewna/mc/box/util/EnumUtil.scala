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
    names.iterator
    .flatMap(name => Try(clazz.getDeclaredField(name)).toOption)
    .filter(_.isEnumConstant)
    .flatMap(field => Try(field.get(null).asInstanceOf[T]).toOption)
    .find(_ => true) // Make sure we only find one



  def getAllMatching[T <: Enum[T]](clazz: Class[T], names: Seq[String]): Set[T] =
    names.iterator
    .flatMap(name => valueOf(clazz, Seq(name)))
    .toSet
  
  def getMaterial(names: Seq[String]): Option[Material] = valueOf(classOf[Material], names)
  
  def getStatistic(names: Seq[String]): Option[Statistic] = valueOf(classOf[Statistic], names)
  
  def getEntityType(names: Seq[String]): Option[EntityType] = valueOf(classOf[EntityType], names)
}
