package com.amichalo.mooolelo.api

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.model.{MediaTypes, HttpEntity}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.stream.ActorMaterializer
import com.amichalo.mooolelo.service.RegistryService
import RegistryService._
import com.amichalo.mooolelo.api.util.PathMatchers
import com.amichalo.mooolelo.protocol.MoooleloJsonProtocol
import com.amichalo.mooolelo.service.RegistryService
import com.typesafe.scalalogging.LazyLogging
import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

import scala.concurrent.ExecutionContextExecutor


trait MoooleloRouting extends PathMatchers with LazyLogging with MoooleloJsonProtocol with SprayJsonSupport with DefaultJsonProtocol {

  implicit def actorSystem: ActorSystem
  implicit def materializer: ActorMaterializer
  implicit def dispacher: ExecutionContextExecutor

  def service: RegistryService

  val registryRoute = {
    logRequestResult("services", Logging.DebugLevel) {
      pathPrefix("services") {
        pathEndOrSingleSlash {
          get {
            complete {
              service.list.map[ToResponseMarshallable] {
                case ListResponse(list) =>
                  val entity = HttpEntity(MediaTypes.`application/json`, list.toJson.compactPrint)
                  OK -> entity
                case FailedAction => InternalServerError
              }
            }
          } ~
          post {
            entity(as[RegistrationRequest]) { request =>
              complete {
                service.register(request).map {
                  case SuccessfulAction => Created //FIXME should return newly created entity
                  case FailedAction => InternalServerError
                }
              }
            }
          }
        } ~
        path(ServiceTypeMatcher) { serviceType =>
          get {
            complete {
              service.get(serviceType).map[ToResponseMarshallable] {
                case GroupResponse(set) =>
                  val entity = HttpEntity(MediaTypes.`application/json`, set.toJson.compactPrint)
                  OK -> entity
                case NotFoundResponse => NotFound
                case FailedAction => InternalServerError
              }
            }
          }
        } ~
        path(ServiceTypeMatcher / GroupMatcher) { (serviceType, group) =>
          get {
            complete {
              service.get(serviceType, group).map[ToResponseMarshallable] {
                case GroupResponse(set) =>
                  val entity = HttpEntity(MediaTypes.`application/json`, set.toJson.compactPrint)
                  OK -> entity
                case NotFoundResponse => NotFound
                case FailedAction => InternalServerError
              }
            }
          }
        } ~
        path(ServiceTypeMatcher / GroupMatcher / ServiceIdMatcher) { (serviceType, group, serviceId) =>
          get {
            complete {
              service.get(serviceType, group, serviceId).map[ToResponseMarshallable] {
                case RichServiceResponse(rich) =>
                  val entity = HttpEntity(MediaTypes.`application/json`, rich.toJson.compactPrint)
                  OK -> entity
                case NotFoundResponse => NotFound
                case FailedAction => InternalServerError
              }
            }
          } ~
          post {
            entity(as[HeartbeatRequest]) { request =>
              complete {
                service.heartbeat(serviceType, group, serviceId, request).map[ToResponseMarshallable] {
                  case SuccessfulAction => OK
                  case NotFoundResponse => NotFound
                  case FailedAction => InternalServerError
                }
              }
            }
          } ~
          delete {
            complete {
              service.remove(serviceType, group, serviceId).map[ToResponseMarshallable] {
                case RichServiceResponse(rich) =>
                  val entity = HttpEntity(MediaTypes.`application/json`, rich.toJson.compactPrint)
                  OK -> entity
                case NotFoundResponse => NotFound
                case FailedAction => InternalServerError
              }
            }
          }
        }
      }
    }
  }
}
