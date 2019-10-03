package com.mewna.mc.box.components.shop

import org.bukkit.inventory.ItemStack

/**
  * @author amy
  * @since 7/9/19.
  */
case class ShopItem(key: String,
                    name: String,
                    stack: ItemStack,
                    buyPrice: Double,
                    sellPrice: Double)
