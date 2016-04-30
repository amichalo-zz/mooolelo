package com.amichalo.mooolelo.protocol

import com.amichalo.mooolelo.domain._
import com.amichalo.mooolelo.service.RegistryService.{HeartbeatRequest, RegistrationRequest}
import spray.json.DefaultJsonProtocol

trait MoooleloJsonProtocol extends CommonJsonProtocol with DefaultJsonProtocol with AnyValJsonFormat {

  implicit val serviceIdFormat = fromLongJsonFormat[ServiceId](ServiceId.apply, _.value)
  implicit val groupFormat = fromStringJsonFormat[ServiceGroup](ServiceGroup.apply, _.name)
  implicit val serviceTypeFormat = fromStringJsonFormat[ServiceType](ServiceType.apply, _.name)
  implicit val environmentFormat = fromStringJsonFormat[Environment](Environment.apply, _.value)
  implicit val hostnameFormat = fromStringJsonFormat[Hostname](Hostname.apply, _.host)
  implicit val ipFormat = fromStringJsonFormat[IP](IP.apply, _.value)
  implicit val portFormat = fromIntJsonFormat[Port](Port.apply, _.value)
  implicit val serviceConfigFormat = jsonFormat1(ServiceConfig)
  implicit val serviceVersionFormat = fromStringJsonFormat[ServiceVersion](ServiceVersion.apply, _.value)
  implicit val workingDirectoryFormat = fromStringJsonFormat[WorkingDirectory](WorkingDirectory.apply, _.path)
  implicit val jvmSettingsFormat = jsonFormat1(StartupArguments)
  implicit val healthStatusFormat = jsonFormat2(HealthStatus)

  implicit val baseServiceDefinitionFormat = jsonFormat6(BaseServiceDefinition.apply)
  implicit val serviceDefinitionFormat = jsonFormat9(ServiceDefinition.apply)
  implicit val richserviceDefinitionFormat = jsonFormat14(RichServiceDefinition.apply)
  implicit val servicesInGroupFormat = jsonFormat2(ServicesByType)

  implicit val registrationRequestFormat = jsonFormat12(RegistrationRequest)
  implicit val heartbeatRequestFormat = jsonFormat1(HeartbeatRequest)
}
