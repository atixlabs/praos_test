package io.iohk.praos.domain
import scala.collection.immutable.Map

trait GenesisHistory {
  def getGenesisAt(slotNumber: Int): Option[Genesis]
  def appendAt(genesis: Genesis, slotNumber: SlotNumber): GenesisHistory
}

case class GenesisHistoryImpl(genesisMap: Map[Int, Genesis]) extends GenesisHistory {

  override def getGenesisAt(slotNumber: SlotNumber): Option[Genesis] = genesisMap.get(slotNumber)
  override def appendAt(genesis: Genesis, slotNumber: SlotNumber): GenesisHistory =
    copy(genesisMap = genesisMap.updated(slotNumber, genesis))
}