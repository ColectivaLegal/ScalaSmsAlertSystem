package controllers

import config.Auth0Config._
import javax.inject.Inject
import play.api.libs.json.JsValue
import play.api.mvc._

/**
  * UserController
  */
class UserController @Inject() (cc: ControllerComponents)
  extends AbstractController(cc) with play.api.i18n.I18nSupport with AuthenticatedActionSupport {

  def index: Action[AnyContent] = authenticatedAction { implicit request =>
    val idTokenValue = request.session.get(CacheIdTokenKey).get
    val profile = cache.get[JsValue](cacheProfileKey(idTokenValue)).get
    Ok(views.html.user(profile, this))
  }
}
