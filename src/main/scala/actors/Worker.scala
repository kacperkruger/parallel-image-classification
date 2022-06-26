package actors

import actors.Worker.{Execute, Luminance, Result}
import akka.actor.Actor

import java.awt.Color
import java.awt.image.BufferedImage
import scala.math.sqrt

object Worker {
  case class Execute(name: String, task: BufferedImage)
  case class Result(name: String, luminance: Luminance)
  case class Luminance(score: Int)
}

trait WorkerHandler {
  def convertIMGtoRGB(img: BufferedImage): Array[Int] = {
    val w = img.getWidth()
    val h = img.getHeight()
    img.getRGB(0, 0, w, h, null, 0, w)
  }
  def calculateLuminance(image: Array[Int]): Luminance = {
    val sumOfRGB = image.foldLeft((0, 0, 0))((acc, pixel) => {
      val c = new Color(pixel)
      (
        acc._1 + c.getRed,
        acc._2 + c.getGreen,
        acc._3 + c.getBlue
      )
    })
    val R = (sumOfRGB._1.toDouble / image.length) / 255
    val G = (sumOfRGB._2.toDouble / image.length) / 255
    val B = (sumOfRGB._3.toDouble / image.length) / 255
    Luminance(
      (sqrt(
        0.299 * (R * R) + 0.587 * (G * G) + 0.114 * (B * B)
      ) * 100).toInt
    )
  }
}

class Worker extends Actor with WorkerHandler {
  override def receive: Receive = { case Execute(name, task) =>
    sender() ! Result(name, calculateLuminance(convertIMGtoRGB(task)))
  }
}
