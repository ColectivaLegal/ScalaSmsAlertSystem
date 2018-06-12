package controllers

import config.Auth0Config
import config.Auth0Config._
import javax.inject.{Inject, Singleton}
import play.api.cache.CacheApi
import play.api.http.{HeaderNames, MimeTypes}
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Auth0Controller handles callback functions from Auth0.
  */
@Singleton
class Auth0Controller @Inject()(cc: ControllerComponents, cache: CacheApi, ws: WSClient) extends AbstractController(cc) {

  def callback(codeOpt: Option[String] = None, stateOpt: Option[String] = None) = Action.async {
    if (stateOpt == cache.get(CacheStateKey)) {
      (for {
        code <- codeOpt
      } yield {
        getToken(code)
          .flatMap { case (idToken, accessToken) =>
            getUser(accessToken)
              .map { user =>
                cache.set(cacheProfileKey(idToken), user)
                Redirect(routes.UserController.index())
                  .withSession(
                    "idToken" -> idToken,
                    "accessToken" -> accessToken
                  )
              }
          }
          .recover {
            case ex: IllegalStateException => Unauthorized(ex.getMessage)
          }
      }).getOrElse(Future.successful(BadRequest("No parameters supplied")))
    }
    else {
      Future.successful(BadRequest("Invalid state parameter"))
    }
  }

  private def getToken(code: String): Future[(String, String)] = {
    val config = Auth0Config.get()
    ws
      .url(config.auth0TokenEndpoint)
      .withHeaders(HeaderNames.ACCEPT -> MimeTypes.JSON)
      .post(Json.obj(config.tokenResponseFields(code): _*))
      .map { response =>
        for {
          idToken <- (response.json \ "id_token").asOpt[String]
          accessToken <- (response.json \ "access_token").asOpt[String]
        } yield {
          Future.successful((idToken, accessToken))
        }
      }
      .flatMap(_.getOrElse(Future.failed[(String, String)](new IllegalStateException("Tokens not sent"))))
  }

  private def getUser(accessToken: String): Future[JsValue] = {
    ws
      .url(Auth0Config.get().userInfoUrl)
      .withQueryString("access_token" -> accessToken)
      .get()
      .flatMap(response => Future.successful(response.json))
  }
}
