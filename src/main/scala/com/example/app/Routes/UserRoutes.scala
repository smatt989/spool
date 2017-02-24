package com.example.app.Routes

import com.example.app.models._
import com.example.app.{AuthenticationSupport, SessionTokenStrategy, SlickRoutes}
import org.scalatra.Ok

trait UserRoutes extends SlickRoutes with AuthenticationSupport{

  post("/users/create") {
    contentType = formats("json")
    val user = parsedBody.extract[UserCreate]

    val created = User.createNewUser(user)

    created.map(_.toJson)
  }

  post("/users/search") {
    contentType = formats("json")
    val query = {params("query")}

    User.searchUserName(query)
  }

  post("/users/connections/create") {
    contentType = formats("json")
    authenticate()

    val connectionRequest = parsedBody.extract[ConnectionRequestJson]
    val connection = connectionRequest.newConnection(user.id)

    UserConnection.safeSave(connection)
  }

  post("/users/connections/delete") {
    contentType = formats("json")
    authenticate()

    val rejectionRequest = parsedBody.extract[ConnectionDeleteJson]

    UserConnection.removeBySenderReceiverPair(user.id, rejectionRequest.removeUserId).map(_ => "200")
  }

  get("/users/connections/added") {
    contentType = formats("json")
    authenticate()

    UserConnection.getReceiversBySenderId(user.id)
  }

  get("/users/connections/awaiting") {
    contentType = formats("json")
    authenticate()

    val sent = UserConnection.getReceiversBySenderId(user.id)
    val received = UserConnection.getSendersByReceiverId(user.id)

    for {
      s <- sent
      r <- received
    } yield (r diff s)
  }

  get("/users") {
    User.getAll.map(_.map(_.toJson))
  }

  post("/users/tokens"){
    contentType = formats("json")
    authenticate()

    val rawToken = {params("device_token")}

    val deviceToken = DeviceToken(userId = user.id, deviceToken = Some(rawToken))
    DeviceToken.save(deviceToken)
  }

}