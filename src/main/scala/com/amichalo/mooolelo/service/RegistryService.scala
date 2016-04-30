package com.amichalo.mooolelo.service

import com.amichalo.mooolelo.domain._
import com.amichalo.mooolelo.repository.RegistryRepository
import com.amichalo.mooolelo.repository.RegistryRepository.ServiceEntity
import com.amichalo.mooolelo.service.RegistryService._
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{Future, ExecutionContext}

class RegistryService(repository: RegistryRepository)(implicit ec: ExecutionContext) extends LazyLogging {

  def register(request: RegistrationRequest): Future[RegistryServiceResponse] = {
    logger.info(s"Registering service from group: ${request.group.name} with id: ${request.id.value}")
    repository.get(request.serviceType, request.group, request.id).flatMap {
      case None => repository.put(ServiceEntity(request))
      case Some(entity) => repository.update(entity, ServiceEntity(request))
    }.map { entity =>
      logger.info(s"Service from group: ${request.group.name} with id: ${request.id.value} has been successfully registered")
      SuccessfulAction
    }.recover { case _ =>
      logger.error(s"Unable to register service id: ${request.id.value} to group: ${request.group.name}")
      FailedAction
    }
  }

  def list: Future[RegistryServiceResponse] = {
    logger.info("Received services list request")
    repository.list.map { result =>
      val list = result.map { case (serviceType, entities) => ServicesByType(serviceType, entities.map(v => BaseServiceDefinition(v)))}.toList
      ListResponse(list)
    }.recover { case _ =>
      logger.error(s"Unable to list services")
      FailedAction
    }
  }

  def get(serviceType: ServiceType): Future[RegistryServiceResponse] = {
    logger.info(s"Received request to list services of type: ${serviceType.name}")
    repository.get(serviceType).map { result =>
      val set = result.map(v => ServiceDefinition(v))
      GroupResponse(set)
    }.recover { case _ =>
      logger.error(s"Unable to list services from group: ${serviceType.name}")
      FailedAction
    }
  }

  def get(serviceType: ServiceType, group: ServiceGroup): Future[RegistryServiceResponse] = {
    logger.info(s"Received request to list services of type: ${serviceType.name} from group: ${group.name}")
    repository.get(serviceType, group).map { result =>
      val set = result.map(v => ServiceDefinition(v))
      GroupResponse(set)
    }.recover { case _ =>
      logger.error(s"Unable to list services of type: ${serviceType.name} from group: ${group.name}")
      FailedAction
    }
  }

  def get(serviceType: ServiceType, group: ServiceGroup, serviceId: ServiceId): Future[RegistryServiceResponse] = {
    logger.info(s"Received request to get rich service definition for service: ${serviceId.value} from group: ${group.name}")
    repository.get(serviceType, group, serviceId).map {
      case None => NotFoundResponse
      case Some(entity) => RichServiceResponse(RichServiceDefinition(entity))
    }.recover { case _ =>
      logger.error(s"Unable to get service: ${serviceId.value} from group: ${group.name}")
      FailedAction
    }
  }

  def remove(serviceType: ServiceType, group: ServiceGroup, serviceId: ServiceId): Future[RegistryServiceResponse] = {
    logger.info(s"Received request to remove service: ${serviceId.value} of type: ${serviceType.name} from group: ${group.name}")
    repository.remove(serviceId, group).map {
      case None => NotFoundResponse
      case Some(entity) => RichServiceResponse(RichServiceDefinition(entity))
    }.recover { case _ =>
      logger.error(s"Unable to remove service: ${serviceId.value} of type: ${serviceType.name} from group: ${group.name}")
      FailedAction
    }
  }

  def heartbeat(serviceType: ServiceType, group: ServiceGroup, serviceId: ServiceId, request: HeartbeatRequest): Future[RegistryServiceResponse] = {
    logger.debug(s"Received heartbeat from service: ${serviceId.value} of type: ${serviceType.name} from group: ${group.name}, heath: ${request.health}")
    repository.heartbeat(serviceType, group, serviceId, request.health).map {
      case None => NotFoundResponse
      case Some(time) => SuccessfulAction
    }.recover { case _ =>
      FailedAction
    }
  }
}

object RegistryService {
  trait RegistryServiceRequest
  case class RegistrationRequest(
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
                                  workingDirectory: Option[WorkingDirectory]) extends RegistryServiceRequest
  case class HeartbeatRequest(health: HealthStatus) extends RegistryServiceRequest

  trait RegistryServiceResponse
  case object SuccessfulAction extends RegistryServiceResponse
  case object FailedAction extends RegistryServiceResponse
  case class ListResponse(result: List[ServicesByType]) extends RegistryServiceResponse
  case class GroupResponse(result: Set[ServiceDefinition]) extends RegistryServiceResponse
  case object NotFoundResponse extends RegistryServiceResponse
  case class RichServiceResponse(result: RichServiceDefinition) extends RegistryServiceResponse
}