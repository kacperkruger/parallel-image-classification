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

trait IngestionHandler {
  def getListOfImages(dirPath: String): List[File] = {
    val dir = new File(dirPath)
    dir.listFiles.filter(_.isFile).toList
  }

  def writeNewFile(
      name: String,
      label: LuminanceLabel,
      score: Int,
      inDir: String,
      outDir: String
  ): Unit = {
    val splitName = name.split('.')
    val image =
      ImageIO.read(new File(s"${inDir}/${splitName.head}.${splitName.last}"))
    ImageIO.write(
      image,
      splitName.last,
      new File(
        s"${outDir}/${splitName.head}_${label.value}_$score.${splitName.last}"
      )
    )
  }
}
