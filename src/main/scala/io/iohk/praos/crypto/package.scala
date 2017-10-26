package io.iohk.praos

import akka.util.ByteString

package object crypto {
  type Key = ByteString

  /**
    * The seed for a PRNG.
    */
  type Seed = Int
}
