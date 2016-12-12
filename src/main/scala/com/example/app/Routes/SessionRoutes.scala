package com.example.app.Routes

import com.example.app.models.UserSession
import com.example.app.{MyAuthentication, SlickRoutes}
import org.scalatra.Cookie

/**
  * Created by matt on 12/11/16.
  */
trait SessionRoutes extends SlickRoutes with MyAuthentication{

  get("/sessions") {
    //request.getRemoteUser
    UserSession.getAll
  }

  get("/sessions/new"){
    authenticate()
    redirect("/")
  }

  get("/sessions/logout"){
    authenticate()
    val id = user.id
    scentry.logout()
    scentry.store.invalidate()
    val session = UserSession.fromUser(id)
    session.map(s => UserSession.delete(s.id))
    "200"
  }

}
