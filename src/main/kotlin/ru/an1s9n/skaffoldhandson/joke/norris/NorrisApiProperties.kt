package ru.an1s9n.skaffoldhandson.joke.norris

import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "norris-api")
data class NorrisApiProperties(
  val baseUrl: String,
  val connectTimeoutMillis: Int,
  val readTimeoutMillis: Int,
) {

  private val log = LoggerFactory.getLogger(this.javaClass)

  init {
    log.info("initialized $this")
  }
}
