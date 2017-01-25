package com.example.app.models

import com.example.app.{AppGlobals, HasIntId, Tables, Updatable}

import scala.concurrent.Future
import scala.io.Source
//import slick.driver.H2Driver.api._
import slick.driver.PostgresDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.parsing.json.JSON

case class Adventure(id: Int = 0, creatorUserId: Int, name: String = Adventure.randomizeName, description: Option[String] = None) extends HasIntId[Adventure]{
  def updateId(id: Int) = this.copy(id = id)

  def toJson(user: UserJson) =
    AdventureJson(id, user, name, description)
}

case class AdventureJson(id: Int, creator: UserJson, name: String, description: Option[String])

object Adventure extends Updatable[Adventure, (Int, Int, String, Option[String]), Tables.Adventures]{

  lazy val table = Tables.adventures

  lazy val googleApiUrl = "https://maps.googleapis.com/maps/api/directions/json?"

  def serializeMany(adventures: Seq[Adventure]) = {
    val creatorIds = adventures.map(_.creatorUserId)
    User.byIds(creatorIds).map(users => {
      val userMap = users.map(u => u.id -> u.toJson).toMap
      adventures.map(adventure => adventure.toJson(userMap(adventure.creatorUserId)))
    })
  }

  def generateDirections(adventureId: Int, optionalOrigin: Option[Waypoint] = None) = {
    val waypoints = Waypoint.byAdventureId(adventureId)
    waypoints.map(ws => {
      val (origin, points) = if(optionalOrigin.isDefined)
        (optionalOrigin.get, ws)
      else
        (ws.head, ws.tail)
      val url = generateUrl(origin = origin, waypoints = points)
      println("URL: "+url)
      val directions = Source.fromURL(url).mkString
      JSON.parseFull(directions)
    })
  }

  def authorizedEditor(adventureId: Int, user: UserJson) = {
    if(adventureId == 0){
      Future.apply(true)
    } else {
      val oldAdv = Adventure.byId(adventureId)
      oldAdv.map(adv => {
        if (adv.creatorUserId == user.id) {
          true
        } else {
          false
        }
      })
    }
  }

  def userSave(adv: Adventure, user: UserJson) = {
    authorizedEditor(adv.id, user).flatMap(authorized => {
      if(authorized){
        save(adv)
      } else {
        throw new IllegalAccessException("Only creator can edit Adventure")
      }
    })
  }

  private[this] def generateUrl(mode: String = "walking", origin: Waypoint, waypoints: Seq[Waypoint]) = {
    googleApiUrl +"mode="+mode+"&"+formatWaypointUrlParams(origin, waypoints)+"&key="+AppGlobals.googleMapsKey
  }

  private[this] def formatWaypointUrlParams(origin: Waypoint, ws: Seq[Waypoint]) = {
    val destination = ws.last
    val via = ws.drop(1)
    val viaParams = if(via.nonEmpty)
        "&waypoints=via:"+via.map(formatLatLongUrlParams).mkString("|")
      else
        ""
    "origin="+formatLatLongUrlParams(origin)+"&destination="+formatLatLongUrlParams(destination)+viaParams
  }

  private[this] def formatLatLongUrlParams(w: Waypoint) =
    w.latitude+","+w.longitude

  def updateQuery(a: Adventure) = table.filter(_.id === a.id)
      .map(x => (x.name, x.description))
        .update((a.name, a.description))

  def reify(tuple: (Int, Int, String, Option[String])): Adventure =
    Adventure(tuple._1, tuple._2, tuple._3, tuple._4)

  def classToTuple(a: Adventure): (Int, Int, String, Option[String]) =
    (a.id, a.creatorUserId, a.name, a.description)

  val names = Seq(
    "Great",
    "Mysterious",
    "Unique",
    "Rugged",
    "Impossible",
    "Mandatory",
    "Critical"
  )

  def randomizeName = {
    val i = names.size
    names(math.floor(math.random * i).toInt) + " Adventure"
  }

}