package com.mewna.mc.box.tile

import com.mewna.mc.box.framework.component.{Component, Single}

import scala.collection.mutable.ArrayBuffer

/**
  * @author amy
  * @since 7/10/19.
  */
@Single
@Component
class SignRegistry {
  val customSigns: ArrayBuffer[CustomSign] = ArrayBuffer[CustomSign]()
}
