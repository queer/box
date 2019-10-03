package com.mewna.mc.box

import com.mewna.mc.box.framework.BoxedPlugin
import com.mewna.mc.box.util.ItemUtil

/**
  * @author amy
  * @since 7/9/19
  */
@Boxed("com.mewna.mc.box")
class Box extends BoxPlugin {
  override def onEnable(): Unit = {
    saveDefaultConfig()
    getConfig.options().copyDefaults(true)
    ItemUtil.setup()
    BoxedPlugin.initialize(this)
  }
}
