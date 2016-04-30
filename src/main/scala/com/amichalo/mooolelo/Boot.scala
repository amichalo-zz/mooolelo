package com.amichalo.mooolelo

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.amichalo.mooolelo.api.MoooleloRouting
import com.amichalo.mooolelo.repository.RegistryRepository
import com.amichalo.mooolelo.repository.inmemory.InMemoryRepository
import com.amichalo.mooolelo.service.RegistryService
import com.typesafe.scalalogging.LazyLogging

import scala.util.{Failure, Success}

object Boot extends App with MoooleloRouting with LazyLogging {

  val actorSystemName: String = "mooolelo"
  //FIXME repositry should be configurable
  implicit lazy val actorSystem = ActorSystem(actorSystemName)
  implicit lazy val materializer = ActorMaterializer()
  implicit lazy val dispacher = actorSystem.dispatcher

  val repository = repositoryFactory()
  override def service: RegistryService = new RegistryService(repository)

  Http().bindAndHandle(interface = Config.interface, port = Config.port, handler = registryRoute) onComplete {
    case Success(_) => logger.info(s"Server has been started on: ${Config.interface}:${Config.port}")
    case Failure(ex) => logger.error(s"Couldn't start the server", ex)
  }

  def repositoryFactory(): RegistryRepository = new InMemoryRepository()
}
