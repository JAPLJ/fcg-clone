package controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Keep}
import arena.{ArenaService, BattleState, ClientInput}
import javax.inject.Inject
import play.api.libs.json.JsValue
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket

class ArenaController @Inject()(implicit val system: ActorSystem,
                                implicit val materializer: Materializer,
                                arenaService: ArenaService) {

  def start(): WebSocket = WebSocket.accept[JsValue, JsValue] { request =>
    val userName = request.getQueryString("name").getOrElse("<anonymous>")
    val (userKey, arena) = arenaService.start()

    val userInput: Flow[JsValue, ClientInput, _] =
      ActorFlow.actorRef[JsValue, ClientInput](out =>
        ClientInputActor.props(out, userKey, userName))
    val userOutput: Flow[BattleState, JsValue, _] =
      ActorFlow.actorRef[BattleState, JsValue](out =>
        ClientOutputActor.props(out, userKey))

    userInput.viaMat(arena.bus)(Keep.right).viaMat(userOutput)(Keep.right)
  }

}
