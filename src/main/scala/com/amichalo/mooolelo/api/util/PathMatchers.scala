package com.amichalo.mooolelo.api.util

import akka.http.scaladsl.server.PathMatchers.{Segment, LongNumber}
import akka.http.scaladsl.server._
import com.amichalo.mooolelo.domain.{ServiceGroup, ServiceType, ServiceId}

trait PathMatchers {

  val ServiceIdMatcher: PathMatcher1[ServiceId] = {
    LongNumber map { value => ServiceId(value) }
  }

  val ServiceTypeMatcher: PathMatcher1[ServiceType] = {
    Segment map { value => ServiceType(value) }
  }

  val GroupMatcher: PathMatcher1[ServiceGroup] = {
    Segment map { value => ServiceGroup(value) }
  }

}
