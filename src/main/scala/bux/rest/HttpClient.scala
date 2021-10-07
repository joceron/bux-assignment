package bux.rest

import bux.rest.domain._
import cats.effect.kernel.{Async, Resource}
import cats.implicits._
import org.http4s.Method.POST
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.circe.CirceEntityDecoder._
import org.http4s._
import org.typelevel.ci.CIStringSyntax

import scala.concurrent.ExecutionContext

trait HttpClient[F[_]] {
  def getAuthToken: F[TokenResponse]
}

object HttpClient {

  def kuCoin[F[_]](ec: ExecutionContext = ExecutionContext.global)(implicit F: Async[F]): Resource[F, HttpClient[F]] =
    BlazeClientBuilder[F](ec).resource.map { client =>
      new HttpClient[F] {
        override def getAuthToken: F[TokenResponse] =
          client.expectOr[TokenResponse](generateRequest)(onError(s"Failed to fetch public token"))

        private def generateRequest: Request[F] = {
          val rootUri = Uri.unsafeFromString("https://api.kucoin.com") // TODO: move this into a .conf file
          Request[F](
              POST
            , rootUri / "api" / "v1" / "bullet-public"
            , headers = Headers(
              Header
                .Raw(ci"Accept", "*/*") // For some reason, I get a 406 Not Acceptable if I don't include this header.
            )
          )
        }

        private def onError(message: String)(resp: Response[F]): F[Throwable] =
          resp
            .bodyText
            .compile
            .toList
            .map(_.mkString)
            .flatMap(body =>
              F.raiseError[Throwable](new IllegalStateException(s"$message, details: ${resp.status} $body"))
            )
      }
    }

}
