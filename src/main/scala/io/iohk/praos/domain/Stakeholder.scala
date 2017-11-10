package io.iohk.praos.domain

import io.iohk.praos.crypto.{Key}

case class Stakeholder(privateKey: Key, publicKey: Key)
