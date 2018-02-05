package io.netty

import play.core.server.ServerProvider

/**
  * to provide CustomNettyServer that will allow custom before server shutdown hook.
  */
class NettyServerProvider extends ServerProvider {
  def createServer(context: ServerProvider.Context) = new CustomNettyServer(
    context.config,
    context.appProvider,
    context.stopHook,
    context.actorSystem
  )(context.materializer)
}