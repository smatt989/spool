package com.example.app.Routes

import com.example.app.models.UserSession
import com.example.app.{AuthenticationSupport, SessionTokenStrategy, SlickRoutes}
import org.scalatra.Ok

trait SessionRoutes extends SlickRoutes with AuthenticationSupport{

  get("/sessions") {
    UserSession.getAll
  }

  get("/sessions/new"){
    contentType = formats("json")
    authenticate()
    user.toJson
  }

  post("/sessions/logout"){
    authenticate()
    val id = user.id
    scentry.logout()
    scentry.store.invalidate()
    val session = UserSession.fromUser(id)
    session.map(s => UserSession.delete(s.id))
    "200"
  }

}