package ScalaCrawler

import java.net.{URI, URL}

import scala.language.implicitConversions

/**
  * @author Foat Akhmadeev
  *         17/01/16
  */
package object Core {
  implicit def string2url(s: String): URI = new URI(s)

  implicit def string2urlWithSpec(s: (String, String)): URI = new URL(new URL(s._1), s._2).toURI
}
