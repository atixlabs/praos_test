package io.iohk.praos.util

import org.slf4j
import org.slf4j.LoggerFactory

trait Logger {
  val log: slf4j.Logger = LoggerFactory.getLogger(getClass)
}

object Logger {
  def getLogger(clazz: Class[_]): org.slf4j.Logger = LoggerFactory.getLogger(clazz)
  def getLogger(name: String): org.slf4j.Logger = LoggerFactory.getLogger(name)
}
