package com.example.app.models

import com.example.app.{AppGlobals, Tables}
import org.joda.time.DateTime
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by matt on 1/27/17.
  */
case class AdventureHeadline(
                              id: Int,
                              creator: UserJson,
                              name: String,
                              description: Option[String],
                              started: Boolean,
                              finished: Boolean,
                              lastUpdate: Option[Long],
                              startCoordinate: LatLng,
                              sharers: Seq[UserJson]
                            )

object AdventureHeadline {

/*  def getUserLocationAdventures(userId: Int, location: LatLng) = {
    ( for {
      nearbyWaypoints <- Adventure.adventuresNear(location)
      myAdventures <- Adventure.adventuresBy(userId)
      progress <- AdventureProgress.getAllLatestProgressesFor(userId)
      shares <- AdventureShare.adventuresSharedByReceiverId(userId)
      advs <- Adventure.byIds((nearbyWaypoints.map(_.adventureId) ++ progress.keys.toSeq ++ shares.map(_.adventure.id)).toSet.toSeq)
      creators <- User.byIds(advs.map(_.creatorUserId) :+ userId)
    } yield (advs ++ myAdventures, shares, progress, nearbyWaypoints.groupBy(_.adventureId).mapValues(_.), creators.map(_.toJson))).map { case (adventures, shares, progress, markers, creators) => {
      reifyAdventureHeaders(adventures, shares, progress, markers, creators)
    }}
  }*/

  def getAllAdventures(userId: Int, location: LatLng) = {
    (for {
      nearbyAdventures <- Adventure.adventuresNear(location)
      myAdventures <- Adventure.adventuresBy(userId)
      shares <- AdventureShare.adventuresSharedByReceiverId(userId)
      progress <- AdventureProgress.getAllLatestProgressesFor(userId)
      adventures <- Adventure.byIds((nearbyAdventures.map(_.adventureId) ++ shares.map(_.adventure.id) ++ progress.keys.toSeq ++ myAdventures.map(_.id)).distinct)
      markers <- Waypoint.getFirstWaypointByAdventureIds(adventures.map(_.id))
      creators <- User.byIds(adventures.map(_.creatorUserId).distinct)
    } yield (adventures, shares, progress, markers, creators.map(_.toJson))).map { case (adventures, shares, progress, markers, creators) => {
      reifyAdventureHeaders(adventures, shares, progress, markers, creators)
    }}
  }

  def getOneAdventure(userId: Int, adventureId: Int) = {
    ((for {
      adventures <- Adventure.byId(adventureId)
      shares <- AdventureShare.adventuresSharedByReceiverIdAndAdventureId(userId, adventureId)
      progress <- AdventureProgress.getLatestProgress(ProgressRequest(userId, adventureId))
      markers <- Waypoint.getFirstWaypointByAdventureIds(Seq(adventureId))
      creators <- User.byId(adventures.creatorUserId)
    } yield (adventures, shares, progress, markers, creators.toJson)).map { case (adventures, shares, progress, markers, creators) => {
      reifyAdventureHeaders(Seq(adventures), shares, Seq(progress).flatMap(_.map(a => a.adventureId -> a)).toMap, markers, Seq(creators))
    }}).map(_.head)
  }

  private[this] def reifyAdventureHeaders(
         adventures: Seq[Adventure],
         shares: Seq[AdventureShareJson],
         progress: Map[Int, AdventureProgress],
         markers: Map[Int, Waypoint],
         creators: Seq[UserJson]) = {
    val now = new DateTime().getMillis
    val creatorsById = creators.map(c => c.id -> c).toMap
    val sharesByAdventureId = shares.groupBy(_.adventure.id)
    val as = adventures.map(adventure => {
      val marker = markers(adventure.id)
      AdventureHeadline(
        adventure.id,
        creatorsById(adventure.creatorUserId),
        adventure.name,
        adventure.description,
        started = progress.get(adventure.id).isDefined,
        finished = progress.get(adventure.id).map(_.finished).getOrElse(false),
        lastUpdate = progress.get(adventure.id).map(_.updatedAt),
        startCoordinate = LatLng(marker.latitude, marker.longitude),
        sharers = sharesByAdventureId.get(adventure.id).map(shares => shares.map(_.sender).distinct).getOrElse(Nil)
      )
    })
    println("finished reify: "+(new DateTime().getMillis - now))
    as
  }
}