package com.example.app.Routes

import com.example.app.SlickRoutes
import com.example.app.models._

trait AppRoutes extends SlickRoutes{


  get("/") {
    <html>
      <body>
        <div id="app"></div>
        <script src="/front-end/dist/bundle.js"></script>
      </body>
    </html>
  }

  post("/adventures/save") {
    contentType = formats("json")

    val adventure = parsedBody.extract[Adventure]

    Adventure.save(adventure)
  }

  get("/adventures/:id") {
    contentType = formats("json")

    val adventureId = {params("id")}.toInt

    Adventure.byId(adventureId)
  }

  get("/adventures/available") {
    contentType = formats("json")

    Adventure.getAll
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
        ss.filter(_.elementType == TriggerAction).map(_.toJson),
        ss.filter(_.elementType == TriggerEvent).map(_.toJson)
      )
    })
  }

}
