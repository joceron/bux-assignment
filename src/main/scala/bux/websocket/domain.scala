package bux.websocket

import io.circe._
import io.circe.generic.semiauto._

object domain {

  implicit val pingMessageDecoder: Encoder[PingMessage] = deriveEncoder

  implicit val decodeResponse: Decoder[Response] = new Decoder[Response] {
    final def apply(c: HCursor): Decoder.Result[Response] =
      for {
        id <- c.downField("id").as[Option[String]]
        respType <- c.downField("type").as[String]
      } yield {
        respType match {
          case "welcome" => WelcomeResponse(id.get)
          case "pong" => PongMessage("")
        }
      }
  }

  trait Response {
    val id: String
  }
  final case class WelcomeResponse(id: String, `type`: String = "welcome") extends Response
  final case class PingMessage(id: String, `type`: String = "ping") extends Response
  final case class PongMessage(id: String, `type`: String = "pong") extends Response
}
