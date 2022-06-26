package actors

import akka.actor.Props

object Supervisor {
  case object Start
  case object Stop

  def props(
      inDir: String,
      outDir: String,
      cutOff: Int,
      numberOfWorker: Int
  ): Props = Props(new Supervisor(inDir, outDir, cutOff, numberOfWorker))
}
