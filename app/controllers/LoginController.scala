package controllers

import java.math.BigInteger
import java.security.SecureRandom

import config.Auth0Config
import controllers.LoginController._
import javax.inject.Inject
import play.api.cache.CacheApi
import play.api.mvc.{AbstractController, ControllerComponents}
import Auth0Config._

/**
  * LoginController is used with Auth0 to permit the user to log in.
  */
class LoginController @Inject() (cc: ControllerComponents, cache: CacheApi) extends AbstractController(cc) {
  def login = Action {
    val state = randomAlphaNumeric()
    cache.set(CacheStateKey, state)
    Redirect(Auth0Config.get().auth0LoginUrl(state))
  }

  def logout = Action(Redirect(Auth0Config.get().auth0LogoutUrl).withNewSession)
}

private object LoginController {
  private val Random = new SecureRandom()

  def randomAlphaNumeric(nrChars: Int = 24): String = new BigInteger(nrChars * 5, Random).toString(32)
}
