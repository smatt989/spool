package com.example.app.models

import com.example.app.Tables.TriggerElementVariables
import com.example.app.{HasIntId, SlickDbObject, Tables}

/**
  * Created by matt on 12/19/16.
  */
case class TriggerElementVariableSpec(triggerElementSubTypeId: Int, name: String, variableArity: VariableArity, variableType: VariableType, order: Int, id: Int) extends HasIntId[TriggerElementVariableSpec] {
  def updateId(id: Int) = this.copy(id = id)

  lazy val toJson = JsonTriggerElementVariableSpec(name, variableArity.asString, variableType.asString)
}

case class JsonTriggerElementVariableSpec(name: String, variableArity: String, variableType: String)

trait VariableArity {
  def asString: String
}

object VariableArity {
  lazy val all = Seq(ArityArray, ArityOne)
  lazy val byString = all.map(a => a.asString -> a).toMap
}

object ArityArray extends VariableArity {
  lazy val asString = "array"
}
object ArityOne extends VariableArity {
  lazy val asString = "one"
}

trait VariableType {
  def asString: String
}

object VariableType {
  lazy val all = Seq(TypeObject, TypeInteger)
  lazy val byString = all.map(a => a.asString -> a).toMap
}

object TypeObject extends VariableType {
  lazy val asString = "object"
}
object TypeInteger extends VariableType {
  lazy val asString = "integer"
}

object TriggerElementVariableSpec extends SlickDbObject[TriggerElementVariableSpec, (Int, Int, String, String, String, Int), TriggerElementVariables]{
  lazy val table = Tables.triggerElementVariables

  def reify(tuple: (Int, Int, String, String, String, Int)): TriggerElementVariableSpec =
    TriggerElementVariableSpec(tuple._2, tuple._3, VariableArity.byString(tuple._4), VariableType.byString(tuple._5), tuple._6, tuple._1)


  def classToTuple(a: TriggerElementVariableSpec): (Int, Int, String, String, String, Int) =
    (a.id, a.triggerElementSubTypeId, a.name, a.variableArity.asString, a.variableType.asString, a.order)
}
