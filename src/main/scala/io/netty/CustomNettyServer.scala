package io.netty

import akka.actor.ActorSystem
import akka.stream.Materializer
import io.shutdown.ApplicationShutdownHandler
import play.api.Logger
import play.core.ApplicationProvider
import play.core.server.{NettyServer, ServerConfig}

import scala.concurrent.Future
import scala.util.control.NonFatal

class CustomNettyServer(config: ServerConfig,
                        override val applicationProvider: ApplicationProvider,
                        stopHook: () => Future[_],
                        override val actorSystem: ActorSystem)(implicit override val materializer: Materializer)
  extends NettyServer(config, applicationProvider, stopHook, actorSystem) {

  private val logger = Logger(this.getClass)

  override def stop() {

    try {
      // call custom application handler before proceed with the actual server stop
      applicationProvider.current.get.injector.instanceOf(classOf[ApplicationShutdownHandler]).stop()
    } catch {
      case NonFatal(e) => logger.error("error while invoke stop on shutdown handler", e)
    }

    super.stop()
  }
}