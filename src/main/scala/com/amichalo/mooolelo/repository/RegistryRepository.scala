package com.amichalo.mooolelo.repository

import com.amichalo.mooolelo.domain._
import com.amichalo.mooolelo.repository.RegistryRepository.ServiceEntity
import com.amichalo.mooolelo.service.RegistryService.RegistrationRequest
import org.joda.time.DateTime

import scala.concurrent.Future

object RegistryRepository {
  case class ServiceEntity(
                            id: ServiceId,
                            serviceType: ServiceType,
                            group: ServiceGroup,
                            environment: Environment,
                            hostname: Hostname,
                            ip: IP,
                            port: Option[Port],
                            health: HealthStatus,
                            version: Option[ServiceVersion],
                            jvmSettings: Option[StartupArguments],
                            config: Option[ServiceConfig],
                            workingDirectory: Option[WorkingDirectory],
                            registrationTimestamp: DateTime,
                            lastHeartbeat: DateTime
                            )

  object ServiceEntity {
    def apply(request: RegistrationRequest): ServiceEntity = {
      val now = DateTime.now
      ServiceEntity(
        id = request.id,
        serviceType = request.serviceType,
        group = request.group,
        environment = request.environment,
        hostname = request.hostname,
        ip = request.ip,
        port = request.port,
        health = request.health,
        version = request.version,
        jvmSettings = request.jvmSettings,
        config = request.config,
        workingDirectory = request.workingDirectory,
        registrationTimestamp = now,
        lastHeartbeat = now
      )
    }
  }

}

trait RegistryRepository {

  def get(serviceType: ServiceType): Future[Set[ServiceEntity]]

  def get(serviceType: ServiceType, group: ServiceGroup): Future[Set[ServiceEntity]]

  def get(serviceType: ServiceType, group: ServiceGroup, serviceId: ServiceId): Future[Option[ServiceEntity]]
  
  def list: Future[Map[ServiceType, Set[ServiceEntity]]]

  def put(service: ServiceEntity): Future[ServiceEntity]

  def update(original: ServiceEntity, updated: ServiceEntity): Future[ServiceEntity]

  def heartbeat(serviceType: ServiceType, group: ServiceGroup, serviceId: ServiceId, health: HealthStatus): Future[Option[DateTime]]

  def remove(serviceId: ServiceId, group: ServiceGroup): Future[Option[ServiceEntity]]
}
