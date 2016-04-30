package com.amichalo.mooolelo

import com.typesafe.config.ConfigFactory

object Config {

  val config = ConfigFactory.load()

  val port = config.getInt("server.port")
  val interface = config.getString("server.interface")
}
