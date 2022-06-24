package reader

import java.io.File

object ImageReader {
  def getListOfFiles(directoryPath: String): Option[List[File]] = {
    val dir = new File(directoryPath)
    if (dir.exists && dir.isDirectory)
      Some(
        dir.listFiles
          .filter(file =>
            file.isFile && (file.toString.contains(".jpg") || file.toString
              .contains(".png"))
          )
          .toList
      )
    else None
  }

}
