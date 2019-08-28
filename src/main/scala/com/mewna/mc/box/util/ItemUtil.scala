package com.mewna.mc.box.util

import com.google.gson.{Gson, JsonParser}
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.{Damageable, ItemMeta, PotionMeta}
import org.bukkit.potion.PotionData

import scala.collection.mutable
import scala.io.Source
import scala.util.Try

/**
 * Adapted from https://github.com/EssentialsX/Essentials/blob/2.x/Essentials/src/com/earth2me/essentials/items/FlatItemDb.java
 *
 * @author amy
 * @since 7/9/19.
 */
object ItemUtil {
  private val GSON = new Gson
  
  // Maps primary name to ItemData
  private val items = mutable.Map[String, ItemData]()
  
  // Maps alias to primary name
  private val itemAliases = mutable.Map[String, String]()
  
  // Every known alias
  private val allAliases = mutable.Set[String]()
  
  private var isSetup = false
  
  def setup(): Unit = {
    if(!isSetup) {
      isSetup = true
      
      // For some reason this didn't work:
      // Source.fromResource("/items.json").getLines.mkString("")
      // The file def. exists, so I'm not sure and don't rly wanna debug it atm...
      val data = Source.fromInputStream(getClass.getResourceAsStream("/items.json")).getLines.mkString("")
      loadJSON(data)
    }
  }

  private def loadJSON(source: String): Unit = {
    val map = new JsonParser().parse(source).getAsJsonObject
    map.entrySet().forEach(e => {
      val key = e.getKey
      val element = e.getValue
      var valid = false
      element match {
        case obj if element.isJsonObject =>
          val data = GSON.fromJson(element, classOf[ItemData])
          items.put(key, data)
          valid = true
        case other =>
          Try {
            val target = element.getAsString
            itemAliases.put(key, target)
            valid = true
          }
      }
      if (valid) {
        allAliases += key
      } else {
        // TODO: How to log a warning here?
      }
    })
  }

  def get(id: String): ItemStack = {
    val lower = id.toLowerCase()
    val split = lower.split(":")
    getByName(split(0)) match {
      case None => throw new NullPointerException("No data for name " + split(0))
      case Some(data) =>
        val material = data.material
        if (!material.isItem) throw new IllegalStateException("Unable to spawn item with material " + material)
        val stack = new ItemStack(material)
        stack.setAmount(material.getMaxStackSize)
        val potionData = data.potionData
        val meta = stack.getItemMeta
        meta match {
          case potion: PotionMeta =>
            potion.setBasePotionData(potionData)
          case _ => // Ignore
        }

        meta match {
          case damageable: Damageable if split.length > 1 =>
            damageable.setDamage(split(1).toInt)
          case _ => // Ignore
        }

        stack.setItemMeta(meta)

        // The spawner provider will update the meta again, so we need to call it after
        // setItemMeta to prevent a race condition
        data.entity match {
          case entity if entity.toString.contains("SPAWNER") => SpawnerUtil.setEntityType(stack, entity)
          case _ =>
        }
        stack
    }
  }

  private def getByName(name: String): Option[ItemData] = {
    val lower = name.toLowerCase()
    items.get(lower).orElse(items.get(itemAliases.getOrElse(lower, lower)))
  }

  def nameList(item: ItemStack): List[String] =
    name(item) match {
      case None => List()
      case Some(name) =>
        itemAliases.keys.filter(_.equalsIgnoreCase(name)).toList ++ List(name)
    }



  def name(item: ItemStack): Option[String] = {
    val data = lookup(item)
    items.find { case (_, value) => value == data }
      .map { case (str, _) => str }
  }

  private def isPotionMeta(meta: ItemMeta): Boolean = meta match {
    case _: PotionMeta => true
    case _ => false
  }

  private def lookup(item: ItemStack): ItemData = item.getType match {
    case _ if MaterialUtil.isPotion(item.getType) && isPotionMeta(item.getItemMeta) =>
      item.getItemMeta match {
        case potion: PotionMeta => new ItemData(material = item.getType, potionData = potion.getBasePotionData)
      }

    case _ if item.getType.toString.contains("SPAWNER") =>
      new ItemData(material = item.getType, entity = SpawnerUtil.getEntityType(item))

    case _ => new ItemData(material = item.getType)
  }

  def listNames: Set[String] = allAliases.toSet

  class ItemData(val material: Material, val potionData: PotionData = null, val entity: EntityType = null) {
    override def hashCode(): Int = 31 * material.## ^ potionData.##

    override def equals(obj: Any): Boolean = obj match {
      case other: ItemData => material == other.material && potionData == other.potionData && entity == other.entity
      case _ => false
    }
  }
  
}
