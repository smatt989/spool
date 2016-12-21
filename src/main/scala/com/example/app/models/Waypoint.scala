package com.example.app.models

import com.example.app.{HasIntId, SlickDbObject, Tables, Updatable}
//import slick.driver.H2Driver.api._
import slick.driver.PostgresDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

case class Waypoint(id: Int = 0, adventureId: Int, name: Option[String] = None, latitude: Double, longitude: Double, order: Int, key: Option[String] = None) extends HasIntId[Waypoint]{
  def updateId(id: Int) = this.copy(id = id)

  def toJson = JsonWaypoint(name, LatLng(latitude, longitude), id)
}

case class JsonWaypoint(title: Option[String] = None, latlng: LatLng, id: Int = 0, key: Option[String] = None){
  def toModel(adventureId: Int, order: Int) =
    Waypoint(id, adventureId, title, latlng.lat, latlng.lng, order, key)
}

object Waypoint extends Updatable[Waypoint, (Int, Int, Option[String], Double, Double, Int), Tables.Waypoints]{
  lazy val table = Tables.waypoints

  def reify(tuple: (Int, Int, Option[String], Double, Double, Int)): Waypoint =
    Waypoint(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6)

  def classToTuple(a: Waypoint): (Int, Int, Option[String], Double, Double, Int) =
    (a.id, a.adventureId, a.name, a.latitude, a.longitude, a.order)

  def byAdventureId(adventureId: Int) =
    db.run(table.filter(_.adventureId === adventureId).result).map(_.map(reify).sortBy(_.order))

  def updateQuery(a: Waypoint) = table.filter(_.id === a.id)
    .map(x => (x.latitude, x.longitude, x.order, x.name))
    .update((a.latitude, a.longitude, a.order, a.name))

  def deleteByAdventureIdQuery(adventureId: Int) =
    table.filter(_.adventureId === adventureId).delete

  def deleteByAdventureId(adventureId: Int) =
    db.run(deleteByAdventureIdQuery(adventureId))

  def saveAdventureWaypoints(adventureId: Int, waypoints: Seq[Waypoint]) =
    db.run(DBIO.seq(deleteByAdventureIdQuery(adventureId), createQuery(waypoints)).transactionally)

}

case class LatLng(lat: Double, lng: Double)