package bux.rest

import io.circe._
import io.circe.generic.semiauto._

object domain {
  implicit val instanceServerDecoder: Decoder[InstanceServer] = deriveDecoder
  implicit val authDataDecoder: Decoder[AuthData] = deriveDecoder
  implicit val tokenResponseDecoder: Decoder[TokenResponse] = deriveDecoder

  final case class TokenResponse(code: Int, data: AuthData)
  final case class AuthData(instanceServers: List[InstanceServer], token: String)
  final case class InstanceServer(endpoint: String, protocol: String, encrypt: Boolean, pingInterval: Int, pingTimeout: Int)
}
