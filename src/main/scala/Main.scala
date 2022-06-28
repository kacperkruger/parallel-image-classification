import actors.Supervisor
import actors.Supervisor.Start
import akka.actor.ActorSystem
import reader.ConfigReader.{Config, readConfig}

object Main extends App {
  val system = ActorSystem("ImageProcessing")
  val conf = readConfig
  val numberOfWorkers = 100
  val cutOffPoint = 30
  conf match {
    case Some(Config(inDir, outDir)) =>
      val supervisor =
        system.actorOf(
          Supervisor.props(inDir, outDir, cutOffPoint, numberOfWorkers)
        )
      supervisor ! Start
    case None => println("config is not provided")
  }
}
