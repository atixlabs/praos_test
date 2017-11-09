package io.iohk.praos.util

import akka.util.ByteString


object ByteUtils {
  def xor(x: ByteString, y: ByteString): ByteString =
    ByteString((x zip y).map(elements => (elements._1 ^ elements._2).toByte).toArray)
}
