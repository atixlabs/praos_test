package io.iohk.praos.domain

/**
  * @param initialTime must be greater or equal to Zero.
  */
class TimeProvider(val initialTime: Int) {
  var timeCounter = 0
  def getTime: Int = initialTime + timeCounter
  /**
    * @param step must be greater than Zero
    */
  def advance(step: Int) = timeCounter += step
}
