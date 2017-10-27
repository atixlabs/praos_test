package io.iohk.praos.domain

import io.iohk.praos.crypto.Hasher


case class Block(state: Option[Hasher#Digest]) {
  // TODO: Implement
}
