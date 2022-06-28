package reader

import io.circe.generic.JsonCodec
import io.circe.parser.parse

object ConfigReader {
  @JsonCodec case class Config(
      inputDirectory: String,
      outputDirectory: String
  )

  def readConfig: Option[Config] = {
    val configString = scala.io.Source.fromResource("config.json").mkString
    val parsedConfig = parse(configString).toOption
    for {
      configJson <- parsedConfig
      config <- configJson.as[Config].toOption
    } yield config
  }
}
