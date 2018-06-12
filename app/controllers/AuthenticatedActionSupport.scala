package controllers

import config.Auth0Config._
import javax.inject.Inject
import play.api.cache.CacheApi
import play.api.libs.json.JsValue
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
  * AuthenticatedActionSupport adds support for a request to require the user to be authenticated.
  */
trait AuthenticatedActionSupport extends Results {

  @Inject() var cache: CacheApi = _

  def authenticatedAction(action: Request[AnyContent] => Result): Action[AnyContent] = Action { request =>
    profileKey(request)
      .map { _ => action(request) }
      .orElse(Some(Redirect(routes.LoginController.login())))
      .get
  }

  def authenticatedActionAsync(action: Request[AnyContent] => Future[Result])(implicit ec: ExecutionContext): Action[AnyContent] = Action.async { request =>
    profileKey(request)
      .map { _ => action(request) }
      .orElse(Some(Future(Redirect(routes.LoginController.login()))))
      .get
  }

  def isAuthenticated(request: Request[AnyContent]): Boolean = profileKey(request).isDefined

  private def profileKey(request: Request[AnyContent]): Option[JsValue] = {
    request
      .session
      .get(CacheIdTokenKey)
      .flatMap(idTokenValue => cache.get[JsValue](cacheProfileKey(idTokenValue)))
  }
}
