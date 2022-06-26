package actors

import actors.Ingestion.{Image, StartIngestion}
import actors.Master.{Aggregate, LuminanceLabel}
import actors.Supervisor.Stop
import akka.actor.{Actor, ActorRef, Props}

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import scala.language.postfixOps

object Ingestion {
  case object StartIngestion
  case class Image(name: String, img: BufferedImage)

  def props(
      inDir: String,
      outDir: String,
      cutOff: Int,
      amountOfWorkers: Int
  ): Props = Props(
    new Ingestion(inDir, outDir, cutOff, amountOfWorkers)
  )
}
