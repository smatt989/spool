package com.example.app

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import com.example.app.models.{User, UserLogin, UserSession}
import org.scalatra.auth.ScentryAuthStore.SessionAuthStore
import org.scalatra.auth.strategy.BasicAuthSupport
import org.scalatra.ScalatraBase
import org.scalatra.auth.{ScentryConfig, ScentryStrategy, ScentrySupport}

object SessionTokenStrategy {
  val HeaderKey = "Spool-Session-Key"
  val Username = "username"
  val Email = "email"
  val Password = "password"
}

class SessionTokenStrategy(protected val app: ScalatraBase) extends ScentryStrategy[User] {
  import SessionTokenStrategy._

  private[this] def getHeader(implicit request: HttpServletRequest) = {
    val headerResult = app.request.getHeader(HeaderKey)
    Option(headerResult)
  }

  private[this] def getToken(implicit request: HttpServletRequest) =
    getHeader

  override def isValid(implicit request: HttpServletRequest): Boolean = {
    getToken.isDefined

  }

  def authenticate()(implicit request: HttpServletRequest, response: HttpServletResponse): Option[User] = {
    val token = getToken
    token.flatMap {t =>  UserSession.byHashString(t).map(_.user) }
  }
}

class PasswordStrategy(protected val app: ScalatraBase) extends ScentryStrategy[User] {

  override def isValid(implicit request: HttpServletRequest) =
    (request.getHeader(SessionTokenStrategy.Username) != null || request.getHeader(SessionTokenStrategy.Email) != null) && request.getHeader(SessionTokenStrategy.Password) != null

  def authenticate()(implicit request: HttpServletRequest, response: HttpServletResponse): Option[User] = {
    val uName = headerOption(request, SessionTokenStrategy.Username)
    val eMail = headerOption(request, SessionTokenStrategy.Email)
    User.authenticatedUser(UserLogin(uName, eMail, request.getHeader(SessionTokenStrategy.Password)))
  }

  private[this] def headerOption(request: HttpServletRequest, key: String) =
    Option(request.getHeader(key))
}

trait AuthenticationSupport extends ScentrySupport[User] with BasicAuthSupport[User] {
  self: ScalatraBase =>

  protected def fromSession = { case id: String =>
    val session = UserSession.byHashString(id)
    session.map(_.user).get
  }
  protected def toSession = { case usr: User ⇒ UserSession.findFromUserOrCreate(usr.id).hashString }

  /**
    * Registers authentication strategies.
    */

  protected val scentryConfig = new ScentryConfig {}.asInstanceOf[ScentryConfiguration]

  override protected def configureScentry {

    scentry.store = new SessionAuthStore(self) {
      override def set(value: String)(implicit request: HttpServletRequest, response: HttpServletResponse) = {
        super.set(value)
        response.headers(SessionTokenStrategy.HeaderKey) = value
      }

      override def get(implicit request: HttpServletRequest, response: HttpServletResponse) = {
        request.header(SessionTokenStrategy.HeaderKey).get
      }

      override def invalidate()(implicit request: HttpServletRequest, response: HttpServletResponse) = {
        response.headers(SessionTokenStrategy.HeaderKey) = null
      }
    }
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