package bux.websocket

import bux.websocket.domain.{PingMessage, Response => WSResponse, _}
import cats.effect.{IO, Ref}
import cats.effect.kernel.{Async, Resource}
import cats.implicits._
import fs2.Stream
import io.circe
import io.circe.parser.decode
import io.circe.syntax._
import org.http4s._
import org.http4s.jdkhttpclient.{JdkWSClient, WSConnection, WSFrame, WSRequest}

import java.net.http.HttpClient
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration._

trait SocketClient[F[_]] {
  def start: F[Unit]
}

object SocketClient {

  def kuCoin[F[_]](uri: String, token: String)(implicit F: Async[F]): Resource[F, SocketClient[F]] = {
    def decodeResponse(rawResponse: String): Either[circe.Error, WSResponse] = decode[WSResponse](rawResponse)

    def handleResponse(connection: WSConnection[F], response: WSResponse): F[Unit] = for {
      _ <- F.delay(println(s"Inside the bux websocket: $response"))
      _ <- repeatable(response.id, connection).compile.drain
//      _ <- connection.send(WSFrame.Text(PingMessage(response.id).asJson.toString()))
    } yield ()

    def repeatable(id: String, connection: WSConnection[F]) =
      Stream.fixedDelay[F](3.second).evalMap(_ => connection.send(WSFrame.Text(PingMessage(id).asJson.toString())))

    Resource.eval(F.delay(HttpClient.newHttpClient())).flatMap(httpClient => JdkWSClient[F](httpClient)).map {
      websocketClient =>
        new SocketClient[F] {
          override def start: F[Unit] = {
            val connectUri = Uri.unsafeFromString(s"$uri?token=$token")
            websocketClient
              .connect(WSRequest(connectUri))
              .use { connection =>
                for {
                  connectId <- Ref.of[F, String]("")
                  received <- F.delay(connection
                                .receiveStream
                                .collect { case WSFrame.Text(text, _) =>
                                  println(text)
                                  decodeResponse(text) }
                                .evalMap {
                                  case Right(response) => for {
                                    _ <- F.delay(println(s"Inside the bux websocket: $response"))
                                    _ <- connectId.set(response.id)
//                                    _ <- connectId.get.flatMap(id =>  connection.send(WSFrame.Text(PingMessage(id).asJson.toString())))
                                  } yield ()
                                  case Left(error)    => F.raiseError[Unit](error)
                                })
//                                .compile
//                                .toList
                sending <- connectId.get.map(id =>  repeatable(id, connection))
                union <- received.merge(sending).compile.drain
                } yield union // received.mkString(" ")
              }
          }
        }
    }
  }

}
