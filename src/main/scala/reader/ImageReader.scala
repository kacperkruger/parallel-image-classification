package reader

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object ImageReader {
  def getListOfFiles(dir: File): List[BufferedImage] = {
    dir.listFiles.filter(_.isFile).toList.map(ImageIO.read)
  }

}
