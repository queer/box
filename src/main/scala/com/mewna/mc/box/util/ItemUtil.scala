package com.mewna.mc.box.util

import java.util

import com.google.gson.{Gson, JsonParser}
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.{Damageable, PotionMeta}
import org.bukkit.potion.PotionData

import scala.collection.JavaConverters._
import scala.io.Source

/**
 * Adapted from https://github.com/EssentialsX/Essentials/blob/2.x/Essentials/src/com/earth2me/essentials/items/FlatItemDb.java
 *
 * @author amy
 * @since 7/9/19.
 */
object ItemUtil {
  private val GSON = new Gson
  
  // Maps primary name to ItemData
  private val items = new util.HashMap[String, ItemUtil.ItemData]
  
  // Maps alias to primary name
  private val itemAliases = new util.HashMap[String, String]
  
  // Every known alias
  private val allAliases = new util.HashSet[String]
  
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
      if(element.isJsonObject) {
        val data = GSON.fromJson(element, classOf[ItemUtil.ItemData])
        items.put(key, data)
        valid = true
      } else {
        try {
          val target = element.getAsString
          itemAliases.put(key, target)
          valid = true
        } catch {
          case _: Exception =>
        }
      }
      if(valid) {
        allAliases.add(key)
      } else {
        // TODO: How to log a warning here?
      }
    })
  }
  
  def get(id: String): ItemStack = {
    val lower = id.toLowerCase()
    val split = lower.split(":")
    val maybe = getByName(split(0))
    if(maybe.isEmpty) {
      throw new NullPointerException("No data for name " + split(0))
    }
    val data = maybe.get
    val material = data.getMaterial
    if(!material.isItem) {
      throw new IllegalStateException("Unable to spawn item with material " + material)
    }
    val stack = new ItemStack(material)
    stack.setAmount(material.getMaxStackSize)
    val potionData = data.getPotionData
    val meta = stack.getItemMeta
    if(potionData != null && meta.isInstanceOf[PotionMeta]) {
      val potionMeta = meta.asInstanceOf[PotionMeta]
      potionMeta.setBasePotionData(potionData)
    }
    // For some reason, Damageable doesn't extend ItemMeta but CB implements them in the same
    // class. As to why, your guess is as good as mine.
    if(split.length > 1 && meta.isInstanceOf[Damageable]) {
      val damageMeta = meta.asInstanceOf[Damageable]
      damageMeta.setDamage(split(1).toInt)
    }
    stack.setItemMeta(meta)
    // The spawner provider will update the meta again, so we need to call it after
    // setItemMeta to prevent a race condition
    val entity = data.getEntity
    if(entity != null && material.toString.contains("SPAWNER")) {
      SpawnerUtil.setEntityType(stack, entity)
    }
    stack
  }
  
  private def getByName(name: String): Option[ItemUtil.ItemData] = {
    val lower = name.toLowerCase()
    if(items.containsKey(lower)) {
      Option(items.get(lower))
    } else if(itemAliases.containsKey(lower)) {
      Option(items.get(itemAliases.get(lower)))
    } else {
      Option.empty
    }
  }
  
  def nameList(item: ItemStack): util.List[String] = {
    val names = new util.ArrayList[String]
    val primaryName = name(item).get
    names.add(primaryName)
    itemAliases.forEach((k, _) => if(k.equalsIgnoreCase(primaryName)) {
      names.add(k)
    })
    names
  }
  
  def name(item: ItemStack): Option[String] = {
    val data = lookup(item)
    for(elem <- items.asScala) {
      if(elem._2 == data) {
        return Option(elem._1)
      }
    }
    Option.empty
  }
  
  private def lookup(item: ItemStack) = {
    val `type` = item.getType
    if(MaterialUtil.isPotion(`type`) && item.getItemMeta.isInstanceOf[PotionMeta]) {
      val potion = item.getItemMeta.asInstanceOf[PotionMeta].getBasePotionData
      new ItemUtil.ItemData(`type`, potion)
    } else if(`type`.toString.contains("SPAWNER")) {
      val entity = SpawnerUtil.getEntityType(item)
      new ItemUtil.ItemData(`type`, entity)
    } else {
      new ItemUtil.ItemData(`type`)
    }
  }
  
  def listNames: util.HashSet[String] = new util.HashSet[String](allAliases)
  
  class ItemData {
    private var material: Material = _
    private var potionData: PotionData = _
    private var entity: EntityType = _
    
    def this(material: Material) {
      this()
      this.material = material
    }
    
    def this(material: Material, potionData: PotionData) {
      this()
      this.material = material
      this.potionData = potionData
    }
    
    def this(material: Material, entity: EntityType) {
      this()
      this.material = material
      this.entity = entity
    }
    
    //noinspection HashCodeUsesVar
    override def hashCode: Int = 31 * material.hashCode ^ potionData.hashCode
    
    override def equals(o: Any): Boolean = {
      if(o == null) {
        return false
      }
      if(!o.isInstanceOf[ItemUtil.ItemData]) {
        return false
      }
      val that = o.asInstanceOf[ItemUtil.ItemData]
      (getMaterial eq that.getMaterial) && potionDataEquals(that) && entityEquals(that)
    }
    
    def getMaterial: Material = {
      material
    }
    
    def getPotionData: PotionData = potionData
    
    def getEntity: EntityType = entity
    
    private def potionDataEquals(o: ItemUtil.ItemData) =
      if(potionData == null && o.getPotionData == null) {
        true
      } else if(potionData != null && o.getPotionData != null) {
        potionData == o.getPotionData
      }
      else {
        false
      }
    
    private def entityEquals(o: ItemUtil.ItemData) = if(entity == null && o.getEntity == null) { // neither have an entity
      true
    }
    else if(entity != null && o.getEntity != null) { // both have an entity; check if it's the same one
      entity eq o.getEntity
    }
    else { // one has an entity but the other doesn't, so they can't be equal
      false
    }
  }
  
}
