package io.iohk.praos.domain

import akka.util.ByteString

case class BlockchainState(blockchain: Blockchain, maybeHeadBlockHash: Option[ByteString], receivedChains: List[Blockchain])


