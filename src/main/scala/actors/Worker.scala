package actors

object Worker {
  case class Execute(id: Int, task: Array[Int])
  case class Result(id: Int, luminance: Luminance)
  case class Luminance(score: Int)
}
