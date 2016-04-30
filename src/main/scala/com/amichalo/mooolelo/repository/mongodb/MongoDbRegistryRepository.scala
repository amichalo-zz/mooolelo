package com.amichalo.mooolelo.repository.mongodb

import com.amichalo.mooolelo.domain._
import com.amichalo.mooolelo.repository.RegistryRepository
import com.amichalo.mooolelo.repository.RegistryRepository.ServiceEntity
import com.typesafe.scalalogging.LazyLogging
import org.joda.time.DateTime

import scala.concurrent.Future

class MongoDbRegistryRepository extends RegistryRepository with LazyLogging {

  override def get(serviceType: ServiceType): Future[Set[ServiceEntity]] = ???

  override def get(serviceType: ServiceType, group: ServiceGroup): Future[Set[ServiceEntity]] = ???

  override def get(serviceType: ServiceType, group: ServiceGroup, serviceId: ServiceId): Future[Option[ServiceEntity]] = ???

  override def put(service: ServiceEntity): Future[ServiceEntity] = ???

  override def remove(serviceId: ServiceId, group: ServiceGroup): Future[Option[ServiceEntity]] = ???

  override def list: Future[Map[ServiceType, Set[ServiceEntity]]] = ???

  override def update(original: ServiceEntity, updated: ServiceEntity): Future[ServiceEntity] = ???

  override def heartbeat(serviceType: ServiceType, group: ServiceGroup, serviceId: ServiceId, health: HealthStatus): Future[Option[DateTime]] = ???
}
