package com.example.app.Routes

import com.example.app.models.{AdventureShare, ShareAdventureJsonRequest}
import com.example.app.{AuthenticationSupport, SlickRoutes}

/**
  * Created by matt on 1/26/17.
  */
trait AdventureShareRoutes extends SlickRoutes with AuthenticationSupport{

  post("/adventures/share") {
    contentType = formats("json")
    authenticate()

    val shareRequests = parsedBody.extract[Seq[ShareAdventureJsonRequest]]
    val adventureShares = shareRequests.map(_.newAdventureShare(user.id))
    AdventureShare.safeSaveManyForOneSender(adventureShares, user.id)
  }

  get("/adventures/shared") {
    contentType = formats("json")
    authenticate()

    AdventureShare.adventuresSharedBySenderId(user.id)
  }

  get("/adventures/received") {
    contentType = formats("json")
    authenticate()

    AdventureShare.adventuresSharedByReceiverId(user.id)
  }
}
