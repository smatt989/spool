package com.example.app

import com.example.app.models._
import org.joda.time.Duration

import scala.concurrent.Future
import Tables._
import slick.driver.H2Driver.api._

import scala.concurrent.ExecutionContext.Implicits.global

object DataImport {

  def populateData(db: Database) = {
    val adventure = Adventure(description = Some("This is going to be AMAZING"))
    val saved = Adventure.save(adventure)
    saved.map(s => {
      val adventureId = s.id
      val waypoints = Seq(
        JsonWaypoint(latitude = 40.775535, longitude = -73.961310),
        JsonWaypoint(latitude = 40.729324, longitude = -73.981329),
        JsonWaypoint(name = Some("CHA AN!!"), latitude = 40.729612, longitude = -73.988141)
      ).zipWithIndex.map{case (w, index) => w.toModel(adventureId, index)}
      Waypoint.saveAdventureWaypoints(adventureId, waypoints)
    })
  }

}
