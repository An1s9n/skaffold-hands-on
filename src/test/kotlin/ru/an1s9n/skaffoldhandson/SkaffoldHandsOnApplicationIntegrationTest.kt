package ru.an1s9n.skaffoldhandson

import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.Test

class SkaffoldHandsOnApplicationIntegrationTest : AbstractIntegrationTest() {

  @Test
  fun healthTest() {
    Given { port(localManagementPort) } When { get("/actuator/health") } Then { statusCode(200) }
  }
}
