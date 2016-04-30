package com.amichalo.mooolelo.protocol

import org.joda.time.{DateTimeZone, DateTime}
import org.joda.time.format.ISODateTimeFormat
import spray.json._

import scala.util.{Failure, Success, Try}

trait CommonJsonProtocol {

  implicit object DateTimeFormat extends RootJsonFormat[DateTime] {
    val formatter = ISODateTimeFormat.dateTime()

    def write(obj: DateTime): JsValue = { JsString(formatter.print(obj.withZone(DateTimeZone.UTC))) }

    def read(json: JsValue): DateTime = json match {
      case JsString(s) =>
        Try { formatter.parseDateTime(s) } match {
          case Success(r) => r
          case Failure(t) => error(s)
        }
      case _ => error(json.toString())
    }

    def error(v: Any): DateTime = {
      val example = formatter.print(0)
      deserializationError(f"'$v' is not a valid date value. Dates must be in compact ISO-8601 format, e.g. '$example'")
    }
  }

}
