package controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Keep}
import arena.{ArenaService, BattleState, ClientInput}
import fcg.rule.Rule
import fcg.rule.cards.CardManager
import javax.inject.Inject
import play.api.libs.json.JsValue
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket

class ArenaController @Inject()(implicit val system: ActorSystem,
                                implicit val materializer: Materializer,
                                arenaService: ArenaService) {

  def start(): WebSocket = WebSocket.accept[JsValue, JsValue] { request =>
    val userName = request.getQueryString("name").getOrElse("<anonymous>")
    val deck =
      CardManager.parseDeck(request.getQueryString("deck").getOrElse(""))
    require(Rule.MinDeckSize <= deck.length && deck.length <= Rule.MaxDeckSize)

    val (userKey, arena) = arenaService.start()

    val userInput: Flow[JsValue, ClientInput, _] =
      ActorFlow.actorRef[JsValue, ClientInput](out =>
        ClientInputActor.props(out, userKey, userName, deck))
    val userOutput: Flow[BattleState, JsValue, _] =
      ActorFlow.actorRef[BattleState, JsValue](out =>
        ClientOutputActor.props(out, userKey))

    userInput.viaMat(arena.bus)(Keep.right).viaMat(userOutput)(Keep.right)
  }

}
