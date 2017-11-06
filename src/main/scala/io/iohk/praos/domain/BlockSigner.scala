package io.iohk.praos.domain

import akka.util.ByteString
import io.iohk.praos.crypto._


object BlockSigner {
  def signedWith(block: Block, privateKey: Key): Block = {
    // FIXME: Hasher and signature provider are hard-coded for simplicity.
    val publicKey = getPublicKeyFromPrivateKey(privateKey)
    val hasher = PredefinedHasher("MD5")
    val blockData = (block.state, block.slotNumber, block.data, block.proof, block.nonce)
    val serializedData = ByteString(blockData.toString)
    val hashedData = hasher.hash(serializedData)
    val blockSignature = SignatureProviderImpl.getSignature(hashedData, privateKey)
    block.copy(signature = blockSignature)
  }

  def getPublicKeyFromSignature(block: Block): Key = {
    SignatureProviderImpl.getPublicKeyFromSignature(block.signature)
  }
}
