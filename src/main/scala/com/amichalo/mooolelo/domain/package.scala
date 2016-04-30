package com.amichalo.mooolelo

import com.amichalo.mooolelo.repository.RegistryRepository
import RegistryRepository.ServiceEntity
import org.joda.time.DateTime


package object domain {
  case class ServiceId(value: Long) extends AnyVal

  case class ServiceType(name: String) extends AnyVal
  
  case class ServiceGroup(name: String) extends AnyVal

  case class Hostname(host: String) extends AnyVal

  case class IP(value: String) extends AnyVal

  case class Port(value: Int) extends AnyVal

  case class Environment(value: String) extends AnyVal

  case class ServiceVersion(value: String) extends AnyVal

  case class WorkingDirectory(path: String) extends AnyVal

  case class ServiceConfig(settings: Map[String, String])

  case class StartupArguments(arguments: List[String])

  case class HealthStatus(isHealthy: Boolean, reason: Option[String])

  case class BaseServiceDefinition(id: ServiceId, serviceType: ServiceType, serviceGroup: ServiceGroup, environment: Environment, hostname: Hostname, health: HealthStatus)
  object BaseServiceDefinition {
    def apply(entity: ServiceEntity): BaseServiceDefinition = {
      BaseServiceDefinition(
        id = entity.id,
        serviceType = entity.serviceType,
        serviceGroup = entity.group,
        environment = entity.environment,
        hostname = entity.hostname,
        health = entity.health
      )
    }
  }

  case class ServiceDefinition(id: ServiceId, serviceType: ServiceType, serviceGroup: ServiceGroup, environment: Environment, hostname: Hostname, ip: IP, port: Option[Port], version: Option[ServiceVersion], health: HealthStatus)
  object ServiceDefinition {
    def apply(entity: ServiceEntity): ServiceDefinition = {
      ServiceDefinition(
        id = entity.id,
        serviceType = entity.serviceType,
        serviceGroup = entity.group,
        environment = entity.environment,
        hostname = entity.hostname,
        ip = entity.ip,
        port = entity.port,
        version = entity.version,
        health = entity.health
      )
    }
  }

  case class RichServiceDefinition(id: ServiceId, serviceType: ServiceType, serviceGroup: ServiceGroup, environment: Environment, hostname: Hostname, ip: IP, port: Option[Port], version: Option[ServiceVersion], state: HealthStatus,
                                   jvmSettings: Option[StartupArguments], config: Option[Map[String, String]], workingDirectory: Option[WorkingDirectory], registrationTimestamp: DateTime, lastHeartbeat: DateTime)
  object RichServiceDefinition {
    def apply(entity: ServiceEntity): RichServiceDefinition = {
      RichServiceDefinition(
        id = entity.id,
        serviceType = entity.serviceType,
        serviceGroup = entity.group,
        environment = entity.environment,
        hostname = entity.hostname,
        ip = entity.ip,
        port = entity.port,
        version = entity.version,
        state = entity.health,
        jvmSettings = entity.jvmSettings,
        config = None, //FIXME
        workingDirectory = entity.workingDirectory,
        registrationTimestamp = entity.registrationTimestamp,
        lastHeartbeat = entity.lastHeartbeat
      )
    }
  }

  case class ServicesByType(name: ServiceType, services: Set[BaseServiceDefinition])
}
