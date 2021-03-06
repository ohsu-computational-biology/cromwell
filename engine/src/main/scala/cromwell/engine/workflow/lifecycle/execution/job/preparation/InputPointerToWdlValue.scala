package cromwell.engine.workflow.lifecycle.execution.job.preparation

import cats.syntax.validated._
import cromwell.core.ExecutionIndex.ExecutionIndex
import common.validation.ErrorOr.ErrorOr
import cromwell.engine.workflow.lifecycle.execution.stores.ValueStore
import shapeless.Poly1
import wom.expression.{IoFunctionSet, WomExpression}
import wom.graph.GraphNode
import wom.graph.GraphNodePort.OutputPort
import wom.values.WomValue

object InputPointerToWdlValue extends Poly1 {
  // Function that can transform any of the coproduct types to an ErrorOr[WomValue]
  type ToWdlValueFn = (GraphNode, Map[String, WomValue], IoFunctionSet, ValueStore, ExecutionIndex) => ErrorOr[WomValue]

  implicit def fromWdlValue: Case.Aux[WomValue, ToWdlValueFn] = at[WomValue] {
    WomValue => (_: GraphNode, _: Map[String, WomValue], _: IoFunctionSet, _: ValueStore, _: ExecutionIndex) =>
      WomValue.validNel: ErrorOr[WomValue]
  }

  implicit def fromOutputPort: Case.Aux[OutputPort, ToWdlValueFn] = at[OutputPort] {
    port => (_: GraphNode, _: Map[String, WomValue], _: IoFunctionSet, valueStore: ValueStore, index : ExecutionIndex) =>
      valueStore.resolve(index)(port)
  }

  implicit def fromWomExpression: Case.Aux[WomExpression, ToWdlValueFn] = at[WomExpression] { 
    womExpression => (_: GraphNode, knownValues: Map[String, WomValue], ioFunctions: IoFunctionSet, _: ValueStore, _ : ExecutionIndex) =>
      womExpression.evaluateValue(knownValues, ioFunctions): ErrorOr[WomValue]
  }
}
