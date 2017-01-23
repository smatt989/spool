package com.example.app

import com.example.app.models._

//import slick.driver.H2Driver.api._
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global

object DataImport {

  def populateData(db: Database) = {
    TriggerElementSpecification.saveAll
/*    val adventure = Adventure(description = Some("This is going to be AMAZING"))
    val saved = Adventure.save(adventure)
    saved.map(s => {
      val adventureId = s.id
      val waypoints = Seq(
        JsonWaypoint(latlng = LatLng(40.775535, -73.961310)),
        JsonWaypoint(latlng = LatLng(40.729324, -73.981329)),
        JsonWaypoint(title = Some("CHA AN!!"), LatLng(40.729612, -73.988141))
      ).zipWithIndex.map{case (w, index) => w.toModel(adventureId, index)}
      Waypoint.saveAdventureWaypoints(adventureId, waypoints)
    })*/
  }

}
