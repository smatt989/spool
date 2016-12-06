package com.example.app.models

import com.example.app.{HasIntId, SlickDbObject, Tables}
import slick.driver.H2Driver.api._
import scala.concurrent.ExecutionContext.Implicits.global

case class Waypoint(id: Int = 0, adventureId: Int, name: Option[String] = None, latitude: Double, longitude: Double, order: Int) extends HasIntId[Waypoint]{
  def updateId(id: Int) = this.copy(id = id)

  def toJson = JsonWaypoint(name, latitude, longitude)
}

case class JsonWaypoint(name: Option[String] = None, latitude: Double, longitude: Double){
  def toModel(adventureId: Int, order: Int) =
    Waypoint(0, adventureId, name, latitude, longitude, order)
}

object Waypoint extends SlickDbObject[Waypoint, (Int, Int, Option[String], Double, Double, Int), Tables.Waypoints]{
  lazy val table = Tables.waypoints

  def reify(tuple: (Int, Int, Option[String], Double, Double, Int)): Waypoint =
    Waypoint(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6)

  def classToTuple(a: Waypoint): (Int, Int, Option[String], Double, Double, Int) =
    (a.id, a.adventureId, a.name, a.latitude, a.longitude, a.order)

  def byAdventureId(adventureId: Int) =
    db.run(table.filter(_.adventureId === adventureId).result).map(_.map(reify).sortBy(_.order))

  def deleteByAdventureIdQuery(adventureId: Int) =
    table.filter(_.adventureId === adventureId).delete

  def saveAdventureWaypoints(adventureId: Int, waypoints: Seq[Waypoint]) =
    db.run(DBIO.seq(deleteByAdventureIdQuery(adventureId), createQuery(waypoints)).transactionally)

}