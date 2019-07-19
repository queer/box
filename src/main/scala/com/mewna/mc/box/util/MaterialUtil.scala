package com.mewna.mc.box.util

import org.bukkit.{DyeColor, Material}

/**
 * Adapted from https://github.com/EssentialsX/Essentials/blob/2.x/Essentials/src/com/earth2me/essentials/utils/MaterialUtil.java
 *
 * @author amy
 * @since 7/9/19.
 */
object MaterialUtil {
  val SPAWNER: Material = EnumUtil.getMaterial(Seq(
    "MOB_SPAWNER",
    "SPAWNER"
  )).get
  private val BEDS = EnumUtil.getAllMatching(classOf[Material],
    Seq(
      "BED",
      "WHITE_BED",
      "ORANGE_BED",
      "MAGENTA_BED",
      "LIGHT_BLUE_BED",
      "YELLOW_BED",
      "LIME_BED",
      "PINK_BED",
      "GRAY_BED",
      "LIGHT_GRAY_BED",
      "CYAN_BED",
      "PURPLE_BED",
      "BLUE_BED",
      "BROWN_BED",
      "GREEN_BED",
      "RED_BED",
      "BLACK_BED"
    ))
  private val BANNERS = EnumUtil.getAllMatching(classOf[Material],
    Seq(
      "BANNER",
      "WHITE_BANNER",
      "ORANGE_BANNER",
      "MAGENTA_BANNER",
      "LIGHT_BLUE_BANNER",
      "YELLOW_BANNER",
      "LIME_BANNER",
      "PINK_BANNER",
      "GRAY_BANNER",
      "LIGHT_GRAY_BANNER",
      "CYAN_BANNER",
      "PURPLE_BANNER",
      "BLUE_BANNER",
      "BROWN_BANNER",
      "GREEN_BANNER",
      "RED_BANNER",
      "BLACK_BANNER",
      "SHIELD"
    ))
  private val FIREWORKS = EnumUtil.getAllMatching(classOf[Material],
    Seq(
      "FIREWORK",
      "FIREWORK_ROCKET",
      "FIREWORK_CHARGE",
      "FIREWORK_STAR"
    ))
  private val LEGACY_SKULLS = EnumUtil.getAllMatching(classOf[Material],
    Seq(
      "SKULL",
      "SKULL_ITEM"))
  private val LEATHER_ARMOR = EnumUtil.getAllMatching(classOf[Material],
    Seq(
      "LEATHER_HELMET",
      "LEATHER_CHESTPLATE",
      "LEATHER_LEGGINGS",
      "LEATHER_BOOTS"
    ))
  private val MOB_HEADS = EnumUtil.getAllMatching(classOf[Material],
    Seq(
      "SKELETON_SKULL",
      "SKELETON_WALL_SKULL",
      "WITHER_SKELETON_SKULL",
      "WITHER_SKELETON_WALL_SKULL",
      "CREEPER_HEAD",
      "CREEPER_WALL_HEAD",
      "ZOMBIE_HEAD",
      "ZOMBIE_WALL_HEAD",
      "DRAGON_HEAD",
      "DRAGON_WALL_HEAD"
    ))
  private val PLAYER_HEADS = EnumUtil.getAllMatching(classOf[Material],
    Seq("PLAYER_HEAD",
      "PLAYER_WALL_HEAD"))
  // includes TIPPED_ARROW which also has potion effects
  private val POTIONS = EnumUtil.getAllMatching(classOf[Material],
    Seq(
      "POTION",
      "SPLASH_POTION",
      "LINGERING_POTION",
      "TIPPED_ARROW"
    ))
  private val SIGN_POSTS = EnumUtil.getAllMatching(classOf[Material],
    Seq(
      "SIGN",
      "SIGN_POST",
      "ACACIA_SIGN",
      "BIRCH_SIGN",
      "DARK_OAK_SIGN",
      "JUNGLE_SIGN",
      "OAK_SIGN",
      "SPRUCE_SIGN"
    ))
  private val WALL_SIGNS = EnumUtil.getAllMatching(classOf[Material],
    Seq(
      "WALL_SIGN",
      "ACACIA_WALL_SIGN",
      "BIRCH_WALL_SIGN",
      "DARK_OAK_WALL_SIGN",
      "JUNGLE_WALL_SIGN",
      "OAK_WALL_SIGN",
      "SPRUCE_WALL_SIGN"
    ))
  
  def isBed(material: Material): Boolean = BEDS.contains(material)
  
  def isBanner(material: Material): Boolean = BANNERS.contains(material)
  
  def isFirework(material: Material): Boolean = FIREWORKS.contains(material)
  
  def isLeatherArmor(material: Material): Boolean = LEATHER_ARMOR.contains(material)
  
  def isMobHead(material: Material,
                durability: Int): Boolean = {
    if(MOB_HEADS.contains(material)) return true
    LEGACY_SKULLS.contains(material) && durability != 3
  }
  
  def isPlayerHead(material: Material,
                   durability: Int): Boolean = {
    if(PLAYER_HEADS.contains(material)) return true
    LEGACY_SKULLS.contains(material) && durability == 3
  }
  
  def isPotion(material: Material): Boolean = POTIONS.contains(material)
  
  def isSignPost(material: Material): Boolean = SIGN_POSTS.contains(material)
  
  def isWallSign(material: Material): Boolean = WALL_SIGNS.contains(material)
  
  def isSign(material: Material): Boolean = isSignPost(material) || isWallSign(material)
  
  def isSkull(material: Material): Boolean = isPlayerHead(material,
    -1) || isMobHead(material,
    -1)
  
  def getColorOf(material: Material): DyeColor = {
    for(color <- DyeColor.values) {
      if(material.toString.contains(color.name)) return color
    }
    DyeColor.WHITE
  }
}
