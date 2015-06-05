package cromwell.binding.types

import cromwell.binding.values.WdlInteger
import spray.json.JsNumber

case object WdlIntegerType extends WdlType {
  override def toWdlString: String = "Int"

  override protected def coercion = {
    case i: Integer => WdlInteger(i)
    case n: JsNumber => WdlInteger(n.value.intValue)
  }
}