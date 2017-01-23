package com.example.app.Routes

import com.example.app.models.{User, UserCreate}
import com.example.app.{AuthenticationSupport, SessionTokenStrategy, SlickRoutes}
import org.scalatra.Ok

trait UserRoutes extends SlickRoutes with AuthenticationSupport{

  post("/users/create") {

    val user = parsedBody.extract[UserCreate]

    val created = User.createNewUser(user)

    created.map(newUser => {
        Ok(body = "200")
    })
  }

  get("/users") {
    User.getAll.map(_.map(_.toJson))
  }

}