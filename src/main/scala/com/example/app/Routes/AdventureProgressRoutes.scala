package com.example.app.Routes

import com.example.app.models.{AdventureProgress, ProgressJsonInsert, ProgressRequest}
import com.example.app.{AuthenticationSupport, SlickRoutes}

trait AdventureProgressRoutes extends SlickRoutes with AuthenticationSupport{

  post("/adventures/:id/progress/create") {
    contentType = formats("json")
    authenticate()

    val adventureId = {params("id")}.toInt
    val progressRequest = parsedBody.extract[ProgressJsonInsert]
    val toSave = progressRequest.newAdventureProgress(user.id, adventureId)

    AdventureProgress.create(toSave)
  }

  get("/adventures/:id/progress/current") {
    contentType = formats("json")
    authenticate()

    val adventureId = {params("id")}.toInt
    val progressRequest = ProgressRequest(user.id, adventureId)

    //maybe better "no entry" message
    AdventureProgress.getLatestProgress(progressRequest).map(_.getOrElse("200"))
  }
}
