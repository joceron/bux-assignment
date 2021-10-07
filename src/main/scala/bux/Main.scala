package bux

import bux.rest.HttpClient
import bux.websocket.SocketClient
import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    HttpClient.kuCoin[IO]().use { restClient =>
      for {
        authData <- restClient.getAuthToken.map(_.data)
        _        <- IO.delay(println(s"Hello, world. $authData"))
        _        <- SocketClient
                      .kuCoin[IO](authData.instanceServers.head.endpoint, authData.token) // TODO: use headOption here?
                      .use(websocketClient => websocketClient.start)
      } yield ()
    } >> IO.pure(ExitCode.Success) // TODO: call client.shutdown() or .shutdownNow()

}
