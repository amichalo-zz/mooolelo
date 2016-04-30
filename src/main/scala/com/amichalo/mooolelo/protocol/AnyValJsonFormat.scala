package com.amichalo.mooolelo.protocol

import spray.json._


//FIXME implement it using macros
trait AnyValJsonFormat {

  def fromStringJsonFormat[T](factory: String => T, extractor: T => String): RootJsonFormat[T] = new RootJsonFormat[T] {
    override def read(json: JsValue): T = json match {
      case JsString(value) => factory(value)
      case _ => deserializationError("String type expected")
    }

    override def write(value: T): JsValue = {
      JsString(extractor(value))
    }
  }

  def fromIntJsonFormat[T](factory: Int => T, extractor: T => Int): RootJsonFormat[T] = new RootJsonFormat[T] {
    override def read(json: JsValue): T = json match {
      case JsNumber(value) => factory(value.toInt)
      case _ => deserializationError("Int type expected")
    }

    override def write(value: T): JsValue = {
      JsNumber(extractor(value))
    }
  }

  def fromLongJsonFormat[T](factory: Long => T, extractor: T => Long): RootJsonFormat[T] = new RootJsonFormat[T] {
    override def read(json: JsValue): T = json match {
      case JsNumber(value) => factory(value.toLong)
      case _ => deserializationError("Long type expected")
    }

    override def write(value: T): JsValue = {
      JsNumber(extractor(value))
    }
  }

}