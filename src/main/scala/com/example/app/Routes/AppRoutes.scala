package com.example.app.Routes

import com.example.app.{AuthenticationSupport, SlickRoutes}
import com.example.app.models._
import org.json4s.JsonAST.JObject

trait AppRoutes extends SlickRoutes with AuthenticationSupport{


  get("/") {
      <html>
        <head>
          <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/latest/css/bootstrap.min.css" />
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.0.2/dist/leaflet.css" />
            <link rel="stylesheet" href="/front-end/dist/app.css" />
          </head>
          <body>
            <div id="app"></div>
            <script src="/front-end/dist/bundle.js"></script>
          </body>
        </html>
  }

/*  post("/adventures/save") {
    contentType = formats("json")

    val adventure = parsedBody.extract[Adventure]

    Adventure.save(adventure)
  }

  get("/adventures/:id") {
    contentType = formats("json")

    val adventureId = {params("id")}.toInt

    Adventure.byId(adventureId)
  }*/

  get("/adventures") {
    contentType = formats("json")
    Adventure.getAll.flatMap(adventures => {
      Adventure.serializeMany(adventures)
    })
  }

  get("/users/adventures") {
    contentType = formats("json")
    authenticate()
    AdventureHeadline.getAllAdventures(user.id)
  }

  get("/users/adventures/:id") {
    contentType = formats("json")
    authenticate()

    val adventureId = {params("id")}.toInt

    AdventureHeadline.getOneAdventure(user.id, adventureId)
  }

  post("/adventures/:id/waypoints/save") {
    contentType = formats("json")

    val adventureId = {params("id")}.toInt
    val waypointInputs = parsedBody.extract[List[JsonWaypoint]]
    val waypoints = waypointInputs.zipWithIndex.map{ case (w, index) => w.toModel(adventureId, index)}

    val saved = Waypoint.saveAdventureWaypoints(adventureId, waypoints)
    saved.map(k => waypointInputs)
  }

  get("/adventures/:id/waypoints") {
    contentType = formats("json")

    val adventureId = {params("id")}.toInt

    val waypoints = Waypoint.byAdventureId(adventureId)

    waypoints.map(_.map(_.toJson))
  }

  post("/adventures/:id/directions") {
    contentType = formats("json")

    val adventureId = {params("id")}.toInt
    val origin = parsedBody.extract[Option[JsonWaypoint]]

    Adventure.generateDirections(adventureId, origin.map(_.toModel(adventureId, 0)))
  }

  get("/specifications/triggers"){
    contentType = formats("json")

    val specifications = TriggerElementSpecification.getAll

    specifications.map(ss => {
      JsonTriggerSpecification(
        ss.filter(_.elementType == TriggerAction).sortBy(_.id).map(_.toJson),
        ss.filter(_.elementType == TriggerEvent).sortBy(_.id).map(_.toJson)
      )
    })
  }

  post("/adventures/save") {
    contentType = formats("json")
    authenticate()
    val adventure = parsedBody.extract[JsonAdventure].toModel

    val futureAdventureId = FullAdventure.save(adventure, user.toJson)

    futureAdventureId.flatMap(id => FullAdventure.getById(id).map(_.toJson))
  }

  get("/adventures/:id") {
    contentType = formats("json")

    val adventureId = {params("id")}.toInt

    FullAdventure.getById(adventureId).map(_.toJson)
  }

  get("/assignments") {
    contentType = formats("json")

    TriggerVariableAssignment.getAll

  }

}
