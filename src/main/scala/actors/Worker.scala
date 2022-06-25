package actors

import java.awt.Color

object Worker {
  case class Execute(id: Int, task: Array[Int])
  case class Result(id: Int, luminance: Luminance)
  case class Luminance(score: Int)
}

trait WorkerHandler {
  def calculateLuminance(arr: Array[Int]): Double = {
    val sumOfRGB = arr.foldLeft((0, 0, 0))((acc, pixel) => {
      val c = new Color(pixel)
      (acc._1 + c.getRed, acc._2 + c.getGreen, acc._3 + c.getBlue)
    })
    0.2126 * sumOfRGB._1 + 0.75152 * sumOfRGB._2 + 0.0722 * sumOfRGB._3
  }
}
