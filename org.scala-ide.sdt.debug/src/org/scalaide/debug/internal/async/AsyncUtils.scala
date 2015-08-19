package org.scalaide.debug.internal.async

import scala.collection.JavaConverters.asScalaBufferConverter

import org.scalaide.debug.internal.model.JdiRequestFactory
import org.scalaide.debug.internal.model.ScalaDebugTarget
import org.scalaide.logging.HasLogger
import org.scalaide.util.internal.Suppress

import com.sun.jdi.Method
import com.sun.jdi.ReferenceType
import com.sun.jdi.ThreadReference
import com.sun.jdi.request.BreakpointRequest

object AsyncUtils extends HasLogger {
  val AsyncProgramPointKey = "app"
  val RequestOwnerKey = "requestOwner"

  def findAsyncProgramPoint(app: AsyncProgramPoint, tpe: ReferenceType): Option[Method] =
    tpe.allMethods().asScala.find(isAsyncProgramPoint(app))

  def isAsyncProgramPoint(app: AsyncProgramPoint)(m: Method): Boolean =
    !m.isAbstract() &&
      m.name().startsWith(app.methodName) &&
      !m.name().contains("$default") &&
      m.declaringType().name() == app.className

  def installMethodBreakpoint(
    debugTarget: ScalaDebugTarget,
    app: AsyncProgramPoint,
    actor: Suppress.DeprecatedWarning.Actor,
    requestOwner: Option[String] = None,
    threadRef: ThreadReference = null): Seq[BreakpointRequest] = {

    for {
      tpe <- debugTarget.virtualMachine.classesByName(app.className).asScala.toSeq
      method <- findAsyncProgramPoint(app, tpe)
    } yield {
      val req = JdiRequestFactory.createMethodEntryBreakpoint(method, debugTarget)
<<<<<<< HEAD
      debugTarget.eventDispatcher.setActorFor(actor, req)
      req.putProperty(AsyncProgramPointKey, app)
      requestOwner.foreach { req.putProperty(RequestOwnerKey, _) }
=======
      //debugTarget.eventDispatcher.setActorFor(actor, req)
      req.putProperty("app", app)
>>>>>>> Merge with async stack tracer
      if (threadRef ne null)
        req.addThreadFilter(threadRef)
      req.enable()
      logger.debug(s"Installed method breakpoint for ${method.declaringType()}.${method.name}")
      req
    }
  }
}
