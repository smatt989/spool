package com.example.app.Routes

import javax.servlet.http.HttpServletRequest

import com.example.app.models.{User, UserCreate}
import com.example.app.{MyAuthentication, SlickRoutes}

/**
  * Created by matt on 12/11/16.
  */
trait UserRoutes extends SlickRoutes with MyAuthentication{

  post("/users/create") {

    val user = parsedBody.extract[UserCreate]

    val created = User.createNewUser(user)

    redirect("/sessions/new?username="+user.username+"&password="+user.password)
  }

  get("/users") {
    User.getAll.map(_.map(_.toJson))
  }

}
