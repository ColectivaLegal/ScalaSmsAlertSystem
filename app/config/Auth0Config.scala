package config

import play.api.Play
import play.api.libs.json.Json.JsValueWrapper

/**
  * Contains the Auth0 configuration. DO NOT COMMIT THESE VALUES TO GIT. They should be specified via the command line.
  * See the README for examples for how to do this.
  */
case class Auth0Config(secret: String, clientId: String, callbackURL: String, domain: String, audience: String) {

  def definedAudience: String = if(audience != "") audience else s"https://$domain/userinfo"

  def userInfoUrl: String = s"https://$domain/userinfo"

  def auth0LoginUrl(state: String): String = {
    String.format(
      "https://%s/%s",
      domain,
      String.format(
        "authorize?client_id=%s&redirect_uri=%s&response_type=code&scope=openid profile&audience=%s&state=%s",
        clientId,
        callbackURL,
        audience,
        state
      )
    )
  }

  def auth0LogoutUrl: String = s"https://$domain/v2/logout?client_id=$clientId&returnTo=http://localhost:9000"

  def tokenResponseFields(code: String): Array[(String, JsValueWrapper)] = Array(
    "client_id" -> clientId,
    "client_secret" -> secret,
    "redirect_uri" -> callbackURL,
    "code" -> code,
    "grant_type"-> "authorization_code",
    "audience" -> definedAudience
  )

  def auth0TokenEndpoint: String = s"https://$domain/oauth/token"
}

object Auth0Config {
  def get() = Auth0Config(
    Play.current.configuration.get[String]("auth0.clientSecret"),
    Play.current.configuration.get[String]("auth0.clientId"),
    Play.current.configuration.get[String]("auth0.callbackURL"),
    Play.current.configuration.get[String]("auth0.domain"),
    Play.current.configuration.get[String]("auth0.audience")
  )

  val CacheIdTokenKey: String = "idToken"
  val CacheStateKey: String = "state"

  def cacheProfileKey(idTokenValue: String): String = s"${idTokenValue}profile"
}
