package com.example.app

import slick.lifted.TableQuery

import scala.concurrent.Future
import slick.driver.H2Driver.api._
import slick.profile.FixedSqlAction

import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by matt on 12/1/16.
  */
trait SlickDbObject[ScalaClass <: HasIntId[ScalaClass], TupleSignature, SlickTable <: Table[TupleSignature] with HasIdColumn[Int]] {

  def table: TableQuery[SlickTable]
  def reify(tuple: TupleSignature): ScalaClass
  def db = AppGlobals.db()
  def classToTuple(a: ScalaClass): TupleSignature


  def getAll =
    db.run(table.result).map(_.map(a => reify(a)))

  def createMany(as: Seq[ScalaClass]): Future[Seq[ScalaClass]] = {
    val ids = db.run(createQuery(as))
    ids.map(is => zipWithNewIds(as, is))
  }

  def byIds(ids: Seq[Int]): Future[Seq[ScalaClass]] =
    db.run(byIdsQuery(ids)).map(_.map(a => reify(a)))

  def deleteMany(ids: Seq[Int]): Future[Int] =
    db.run(deleteQuery(ids))

  def create(a: ScalaClass): Future[ScalaClass] = createMany(Seq(a)).map(_.head)
  def byId(id: Int): Future[ScalaClass] = byIds(Seq(id)).map(_.head)
  def delete(id: Int): Future[Int] = deleteMany(Seq(id))

  //HELPER QUERY STATEMENTS

  def createQuery(as: Seq[ScalaClass]) =
    (table returning table.map(_.id)) ++= as.map(classToTuple)

  def zipWithNewIds(as: Seq[ScalaClass], ids: Seq[Int]) =
    ids.zipWithIndex.map{ case (id, index) => as(index).updateId(id)}

  def deleteQuery(ids: Seq[Int]) =
    table.filter(_.id inSet ids).delete

  def byIdsQuery(ids: Seq[Int]) =
    table.filter(_.id inSet ids).result

}

trait Updatable[ScalaClass <: HasIntId[ScalaClass], TupleSignature, SlickTable <: Table[TupleSignature] with HasIdColumn[Int]] extends SlickDbObject[ScalaClass, TupleSignature, SlickTable] {
  def updateQuery(a: ScalaClass): FixedSqlAction[Int, NoStream, Effect.Write]

  def save(a: ScalaClass): Future[ScalaClass] = saveMany(Seq(a)).map(_.head)

  def updateOne(a: ScalaClass): Future[ScalaClass] =
    updateMany(Seq(a)).map(_.head)

  def updateMany(as: Seq[ScalaClass]): Future[Seq[ScalaClass]] = {
    val queries = DBIO.sequence(as.map(updateQuery))
    db.run(queries).map(_ => as)
  }

  def saveMany(as: Seq[ScalaClass]): Future[Seq[ScalaClass]] = {
    val withTempId = as.zipWithIndex
    val doNotExist = withTempId.filter(a => a._1.id == 0 || a._1.id == null)
    val createTempIds = doNotExist.map(_._2)
    val created = createMany(doNotExist.map(_._1))
    val createdWithTempIds = created.map(c =>
      createTempIds.zipWithIndex.map{ case (tempId, index) => (c(index), tempId)}
    )
    val doExist = withTempId.filter(_._1.id > 0)
    val doExistTempIds = doExist.map(_._2)
    val updated = updateMany(doExist.map(_._1))
    val updatedWithTempIds = updated.map( u =>
      doExistTempIds.zipWithIndex.map{ case (tempId, index) => (u(index), tempId)}
    )

    createdWithTempIds.flatMap(c => updatedWithTempIds.map(u => {
      (c ++ u).sortBy(_._2).map(_._1)
    }))
  }
}

trait HasIntId[A] {
  def id: Int
  def updateId(id: Int): A
}
