package com.example.app

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import com.example.app.models.{User, UserLogin, UserSession}
import org.scalatra.auth.ScentryAuthStore.CookieAuthStore
import org.scalatra.auth.ScentryAuthStore.SessionAuthStore
import org.scalatra.auth.strategy.BasicAuthSupport
import org.scalatra.{CookieOptions, ScalatraBase}
import org.scalatra.auth.{Scentry, ScentryConfig, ScentryStrategy, ScentrySupport}

/**
  * Created by matt on 12/10/16.
  */
object SessionTokenStrategy {
  val HeaderKey = "SPOOL-API-KEY"
  val ParamsKey = "api_key"
  val CookieKey = "scentry.auth.default.user"
}
class SessionTokenStrategy(protected val app: ScalatraBase) extends ScentryStrategy[User] {
  import SessionTokenStrategy._

  private[this] def getHeader(implicit request: HttpServletRequest) = {
    val headerResult = app.request.getHeader(HeaderKey)
    if(headerResult != null)
      Some(headerResult)
    else
      None
  }

  private[this] def getToken(implicit request: HttpServletRequest) =
    getHeader orElse app.params.get(ParamsKey) orElse app.cookies.get(CookieKey)

  override def isValid(implicit request: HttpServletRequest): Boolean = {
    getToken.isDefined

  }

  def authenticate()(implicit request: HttpServletRequest, response: HttpServletResponse): Option[User] = {
    val token = getToken
    token.flatMap {t =>  UserSession.byHashString(t).map(_.user) }
  }
}

class PasswordStrategy(protected val app: ScalatraBase) extends ScentryStrategy[User] {
  val username = "username"
  val email = "email"
  val password = "password"

  override def isValid(implicit request: HttpServletRequest) =
    (request.getHeader(username) != null || request.getHeader(email) != null) && request.getHeader(password) != null
  //(app.params.get(username).isDefined || app.params.get(email).isDefined) && app.params.get(password).isDefined

  def authenticate()(implicit request: HttpServletRequest, response: HttpServletResponse): Option[User] = {
    val uName = headerOption(request, username)
    val eMail = headerOption(request, email)
    User.authenticatedUser(UserLogin(uName, eMail, request.getHeader(password)))
    //User.authenticatedUser(UserLogin(app.params.get(username), app.params.get(email), app.params(password)))
  }

  private[this] def headerOption(request: HttpServletRequest, key: String) = {
    val h = request.getHeader(key)
    if (h != null) {
      Some(h)
    } else {
      None
    }
  }

}

trait MyAuthentication extends ScentrySupport[User] with BasicAuthSupport[User] {
  self: ScalatraBase =>

  val ScalatraAuthValue = "SPOOL-SCALATRA-AUTH"

  protected def fromSession = { case id: String ⇒ {
    val session = UserSession.byHashString(id)
      session.map(_.user).get
  } }
  protected def toSession = { case usr: User ⇒ UserSession.findFromUserOrCreate(usr.id).hashString }

  /**
    * Registers authentication strategies.
    */

  protected val scentryConfig = (new ScentryConfig {}).asInstanceOf[ScentryConfiguration]

  override protected def configureScentry {

    scentry.store = new SessionAuthStore(self) {
      override def set(value: String)(implicit request: HttpServletRequest, response: HttpServletResponse) = {
        super.set(value)
        response.headers(ScalatraAuthValue) = value
      }

      override def get(implicit request: HttpServletRequest, response: HttpServletResponse) = {
        request.header(ScalatraAuthValue).get
      }

      override def invalidate()(implicit request: HttpServletRequest, response: HttpServletResponse) = {
        response.headers(ScalatraAuthValue) = null
      }
    }

/*    val authCookieOptions = CookieOptions(httpOnly = true)
    scentry.store = new CookieAuthStore(self)(authCookieOptions) {
      def set(value: String) {
        super.set(value)
        response.headers(ScalatraAuthValue) = value
      }

      def get: String = {
        val cookie = super.get
        if (cookie == null || cookie.trim.isEmpty) request.header(ScalatraAuthValue).orNull
        else cookie
      }

      def invalidate() {
        cookies.update(Scentry.scentryAuthKey, "")(authCookieOptions.copy(maxAge = 0))
        response.headers(ScalatraAuthValue) = null
      }
    }*/
    scentry.unauthenticated { unauthenticated() }
  }

  def unauthenticated() ={
    response.setHeader("WWW-Authenticate", "Unable to authenticate")
    halt(401, "Unauthenticated")
  }

  override protected def registerAuthStrategies = {
    scentry.register("user_password", _ ⇒ new PasswordStrategy(self))
    scentry.register("session_token", _ ⇒ new SessionTokenStrategy(self))
  }

}