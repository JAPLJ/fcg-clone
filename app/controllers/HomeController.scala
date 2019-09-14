package controllers

import fcg.rule.cards.CardManager
import javax.inject._
import play.api.Application
import play.api.libs.json.Json
import play.api.mvc._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(appProvider: Provider[Application],
                               implicit val assets: AssetsFinder)
  extends InjectedController {

  implicit def app: Application = appProvider.get()

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index(CardManager.monsterCards, CardManager.spellCards))
  }

  def rules() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.rules())
  }

  def glossary() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.glossary())
  }

  /** API */
  def monsterCards = Action { implicit request: Request[AnyContent] =>
    Ok(Json.toJson(CardManager.monsterCards.map(_.toJson)))
  }

  def spellCards = Action { implicit request: Request[AnyContent] =>
    Ok(Json.toJson(CardManager.spellCards.map(_.toJson)))
  }
}
