package com.mewna.mc.box.util

import java.util

import org.bukkit.entity.EntityType
import org.bukkit.{Material, Statistic}

/**
 * Adapted from https://github.com/EssentialsX/Essentials/blob/2.x/Essentials/src/com/earth2me/essentials/utils/EnumUtil.java
 *
 * @author amy
 * @since 7/9/19.
 */
object EnumUtil {
  def valueOf[T <: Enum[_]](enumClass: Class[T], names: Seq[String]): Option[T] = {
    for(name <- names) {
      try {
        val enumField = enumClass.getDeclaredField(name)
        if(enumField.isEnumConstant) {
          //noinspection ScalaStyle
          return Some(enumField.get(null).asInstanceOf[T])
        }
      } catch {
        case _@(_: NoSuchFieldException | _: IllegalAccessException) =>
      }
    }
    None
  }
  
  def getAllMatching[T <: Enum[_]](enumClass: Class[T], names: Seq[String]): util.Set[T] = {
    val set = new util.HashSet[T]
    for(name <- names) {
      try {
        val enumField = enumClass.getDeclaredField(name)
        if(enumField.isEnumConstant) {
          //noinspection ScalaStyle
          set.add(enumField.get(null).asInstanceOf[T])
        }
      } catch {
        case _@(_: NoSuchFieldException | _: IllegalAccessException) =>
        
      }
    }
    set
  }
  
  def getMaterial(names: Seq[String]): Option[Material] = valueOf(classOf[Material], names)
  
  def getStatistic(names: Seq[String]): Option[Statistic] = valueOf(classOf[Statistic], names)
  
  def getEntityType(names: Seq[String]): Option[EntityType] = valueOf(classOf[EntityType], names)
}
