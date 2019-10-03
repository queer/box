package com.mewna.mc.box.framework.component

import com.mewna.mc.box.BoxPlugin

/**
  * @author amy
  * @since 7/9/19.
  */
trait BoxedComponent {
  def doInit(plugin: BoxPlugin): Boolean = {
    loadConfig(plugin)
    init(plugin)
  }

  def loadConfig(plugin: BoxPlugin): Unit = {}

  def init(plugin: BoxPlugin): Boolean

  def getName: String

  def getDesc: String
}
