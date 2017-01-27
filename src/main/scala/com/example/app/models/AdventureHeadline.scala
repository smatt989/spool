package com.example.app.models

import com.example.app.{AppGlobals, Tables}
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

  def getAllAdventures(userId: Int) = {
    (for {
      adventures <- Adventure.getAll
      shares <- AdventureShare.adventuresSharedByReceiverId(userId)
      progress <- AdventureProgress.getAllLatestProgressesFor(userId)
      markers <- Waypoint.getFirstWaypointByAdventureIds(adventures.map(_.id))
      creators <- User.byIds(adventures.map(_.creatorUserId))
    } yield (adventures, shares, progress, markers, creators.map(_.toJson))).map { case (adventures, shares, progress, markers, creators) => {
      val creatorsById = creators.map(c => c.id -> c).toMap
      val sharesByAdventureId = shares.groupBy(_.adventure.id)
      adventures.map(adventure => {
        val marker = markers(adventure.id)
        AdventureHeadline(
          adventure.id,
          creatorsById(adventure.creatorUserId),
          adventure.name,
          adventure.description,
          started = progress.get(adventure.id).isDefined,
          finished = progress.get(adventure.id).map(_.finished).getOrElse(false),
          lastUpdate = progress.get(adventure.id).map(_.updatedAt.getMillis),
          startCoordinate = LatLng(marker.latitude, marker.longitude),
          sharers = sharesByAdventureId.get(adventure.id).map(shares => shares.map(_.sender)).getOrElse(Nil)
        )
      })
    }}
  }
}