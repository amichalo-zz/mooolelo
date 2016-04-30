package com.amichalo.mooolelo.repository.inmemory

import java.util.concurrent.{TimeUnit, Executors}

import com.amichalo.mooolelo.domain._
import com.amichalo.mooolelo.repository.RegistryRepository
import com.amichalo.mooolelo.repository.RegistryRepository.ServiceEntity
import com.typesafe.scalalogging.LazyLogging
import org.joda.time.DateTime

import scala.collection.concurrent.TrieMap
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class InMemoryRepository(implicit ec: ExecutionContext) extends RegistryRepository with LazyLogging {

  //TODO implement history, after some time service is moved to history
  private val ttl: FiniteDuration = 2 minute
  private val reloadFrequency: FiniteDuration = 20 second
  private val singularExecutor = Executors.newSingleThreadScheduledExecutor()

  private[this] val cache: TrieMap[ServiceId, ServiceEntity] = TrieMap.empty

  initialize

  override def put(service: ServiceEntity): Future[ServiceEntity] = {
    cache.put(service.id, service)

    Future(service)
  }

  override def update(original: ServiceEntity, updated: ServiceEntity): Future[ServiceEntity] = { //FIXME change method signature
    val up = updated.copy(registrationTimestamp = original.registrationTimestamp)

    cache.put(updated.id, up)

    Future.successful(up)
  }

  override def heartbeat(serviceType: ServiceType, group: ServiceGroup, serviceId: ServiceId, health: HealthStatus): Future[Option[DateTime]] = {
    cache.get(serviceId) match {
      case Some(entity) =>
        val now = DateTime.now()
        val updated = entity.copy(lastHeartbeat = now, health = health)
        cache.put(serviceId, updated)
        Future.successful(Some(now))
      case None =>
        logger.warn(s"Received heartbeat for not existing service: id: ${serviceId} group: ${group}")
        Future.successful(None)
    }
  }

  override def get(serviceType: ServiceType): Future[Set[ServiceEntity]] = {
    val set: Set[ServiceEntity] = cache.readOnlySnapshot().values.filter(_.serviceType == serviceType).toSet
    Future.successful(set)
  }

  override def get(serviceType: ServiceType, group: ServiceGroup): Future[Set[ServiceEntity]] = {
    val set: Set[ServiceEntity] =
      cache.readOnlySnapshot().values.filter(se => se.serviceType == serviceType && se.group == group).toSet
    Future.successful(set)
  }

  override def get(serviceType: ServiceType, group: ServiceGroup, serviceId: ServiceId): Future[Option[ServiceEntity]] = {
    val entityOpt = cache.get(serviceId).filter(se => se.serviceType == serviceType && se.group == group)
    Future.successful(entityOpt)
  }

  override def remove(serviceId: ServiceId, group: ServiceGroup): Future[Option[ServiceEntity]] = ???

  override def list: Future[Map[ServiceType, Set[ServiceEntity]]] = {
    val map: Map[ServiceType, Set[ServiceEntity]] = cache.readOnlySnapshot().toMap.values.groupBy(_.serviceType).mapValues(_.toSet)

    Future.successful(map)
  }

  private def clearCache() = {
    logger.info(s"START: Clearing cache, currently there are ${cache.size} services in cache")

    val now = DateTime.now().getMillis
    cache.foreach { case (id, entity) =>
      if (now - entity.lastHeartbeat.getMillis > ttl.toMillis) {
        logger.info(s"Service $id, group: ${entity.group} is not valid... removing")
        cache.remove(id)
      }
    }

    logger.info(s"END: Clearing cache, currently there are ${cache.size} services in cache")
  }

  private def initialize = {
    singularExecutor.scheduleWithFixedDelay( new Runnable {
      override def run(): Unit = {
        try {
          clearCache()
        } catch {
          case e: Exception =>
            logger.error(s"Cannot reloadFrequency cache, exception occurred.", e)
        }
      }
    }, reloadFrequency.toSeconds, reloadFrequency.toSeconds, TimeUnit.SECONDS)
  }
}