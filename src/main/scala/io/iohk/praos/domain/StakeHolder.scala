package io.iohk.praos.domain

import io.iohk.praos.crypto.{Key}

case class StakeHolder(privateKey: Key, publicKey: Key)
