package com.example.app.models

import com.example.app.Tables.TriggerElementSubTypes
import com.example.app.{HasIntId, SlickDbObject, Tables}
import slick.lifted.TableQuery

/**
  * Created by matt on 12/19/16.
  */
case class TriggerElementSubType(name: String, enabled: Boolean, elementType: TriggerElementType, id: Int) extends HasIntId[TriggerElementSubType] {
  def updateId(id: Int) = this.copy(id = id)
}

trait TriggerElementType{
  def asString: String
}

object TriggerElementType {
  lazy val all = Seq(TriggerAction, TriggerEvent)
  lazy val byString = all.map(a => a.asString -> a).toMap
}

object TriggerAction extends TriggerElementType{
  lazy val asString = "action"
}
object TriggerEvent extends TriggerElementType{
  lazy val asString = "event"
}

object TriggerElementSubType extends SlickDbObject[TriggerElementSubType, (Int, String, Boolean, String), TriggerElementSubTypes] {
  lazy val table = Tables.triggerElementSubTypes

  def reify(tuple: (Int, String, Boolean, String)): TriggerElementSubType =
    TriggerElementSubType(tuple._2, tuple._3, TriggerElementType.byString(tuple._4), tuple._1)

  def classToTuple(a: TriggerElementSubType): (Int, String, Boolean, String) =
    (a.id, a.name, a.enabled, a.elementType.asString)
}