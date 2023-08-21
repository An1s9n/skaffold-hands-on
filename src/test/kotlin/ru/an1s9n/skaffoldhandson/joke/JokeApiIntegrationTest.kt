package ru.an1s9n.skaffoldhandson.joke

import com.github.database.rider.core.api.dataset.DataSet
import com.github.tomakehurst.wiremock.http.RequestMethod.GET
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.marcinziolo.kotlin.wiremock.equalTo
import com.marcinziolo.kotlin.wiremock.verify
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import net.javacrumbs.jsonunit.JsonMatchers.jsonStringEquals
import net.javacrumbs.jsonunit.core.Option.IGNORING_EXTRA_FIELDS
import org.assertj.core.api.HamcrestCondition
import org.assertj.db.api.Assertions.assertThat
import org.assertj.db.type.Table
import org.assertj.db.type.Table.Order
import org.hamcrest.core.AnyOf
import org.hamcrest.core.IsEqual
import org.hamcrest.core.StringContains
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ru.an1s9n.skaffoldhandson.AbstractIntegrationTest
import ru.an1s9n.skaffoldhandson.joke.norris.stubNotFound
import ru.an1s9n.skaffoldhandson.joke.norris.stubOkEmptyResponse
import ru.an1s9n.skaffoldhandson.joke.norris.stubOkMalformedResponse
import ru.an1s9n.skaffoldhandson.joke.norris.stubOkValidResponse
import ru.an1s9n.skaffoldhandson.joke.norris.stubServiceUnavailable
import ru.an1s9n.skaffoldhandson.junit.WireMockDynamicPortExtension
import ru.an1s9n.skaffoldhandson.util.APPLICATION_JSON
import ru.an1s9n.skaffoldhandson.util.row
import javax.sql.DataSource

@ExtendWith(WireMockDynamicPortExtension::class)
class JokeApiIntegrationTest(private val dataSource: DataSource) : AbstractIntegrationTest() {

  @Test
  @DataSet("dataset/joke/joke.data.yaml", "dataset/joke/joke-request-log.empty.yaml")
  fun `test db source when db contains jokes`() {
    Given { port(localServerPort) } When { get("/api/v1/joke/random?source=db") } Then {
      statusCode(200)
      contentType(APPLICATION_JSON)
      body(
        jsonStringEquals(
          // language=JSON
          """
          {
            "id": "#{json-unit.regex}^(101|102|103)$",
            "text": "#{json-unit.regex}^(foo|bar|buz)$"
          }
        """
        )
      )
    }

    assertThat(Table(dataSource, "joke_request_log", arrayOf(Order.asc("id"))))
      .hasNumberOfRows(1)
      .row(index = 0) {
        value("source").isEqualTo("DB")
        value("status").isEqualTo("OK")
        value("joke_id").satisfies(HamcrestCondition(AnyOf(IsEqual("101"), IsEqual("102"), IsEqual("103"))))
        value("joke_text").satisfies(HamcrestCondition(AnyOf(IsEqual("foo"), IsEqual("bar"), IsEqual("buz"))))
        value("error_message").isNull
      }
  }

  @Test
  @DataSet("dataset/joke/joke.empty.yaml", "dataset/joke/joke-request-log.empty.yaml")
  fun `test db source when db does not contain jokes`() {
    Given { port(localServerPort) } When { get("/api/v1/joke/random?source=db") } Then { statusCode(404) }

    assertThat(Table(dataSource, "joke_request_log", arrayOf(Order.asc("id"))))
      .hasNumberOfRows(1)
      .row(index = 0) {
        value("source").isEqualTo("DB")
        value("status").isEqualTo("NOT_FOUND")
        value("joke_id").isNull
        value("joke_text").isNull
        value("error_message").isNull
      }
  }

  @Test
  @DataSet("dataset/joke/joke-request-log.empty.yaml")
  fun `test norris api source when server responds ok with valid response`(wireMockRI: WireMockRuntimeInfo) {
    val wireMock = wireMockRI.wireMock.apply { stubOkValidResponse() }

    Given { port(localServerPort) } When { get("/api/v1/joke/random?source=norris-api") } Then {
      statusCode(200)
      contentType(APPLICATION_JSON)
      body(
        jsonStringEquals(
          // language=JSON
          """
          {
            "id": "zuf836jurwgszxegfxe76g",
            "text": "The Bible was originally titled \"Chuck Norris and Friends\""
          }
        """
        )
      )
    }

    assertThat(Table(dataSource, "joke_request_log", arrayOf(Order.asc("id"))))
      .hasNumberOfRows(1)
      .row(index = 0) {
        value("source").isEqualTo("NORRIS_API")
        value("status").isEqualTo("OK")
        value("joke_id").isEqualTo("zuf836jurwgszxegfxe76g")
        value("joke_text").isEqualTo("The Bible was originally titled \"Chuck Norris and Friends\"")
        value("error_message").isNull
      }

    wireMock.verify {
      urlPath equalTo "/jokes/random"
      method = GET
      exactly = 1
    }
  }

  @Test
  @DataSet("dataset/joke/joke-request-log.empty.yaml")
  fun `test norris api source when server responds ok with empty response`(wireMockRI: WireMockRuntimeInfo) {
    val wireMock = wireMockRI.wireMock.apply { stubOkEmptyResponse() }

    Given { port(localServerPort) } When { get("/api/v1/joke/random?source=norris-api") } Then {
      statusCode(500)
      contentType(APPLICATION_JSON)
      body(
        jsonStringEquals(
          // language=JSON
          """
          {
            "message": "java.lang.IllegalStateException: Norris api returned null"
          }
        """
        ).withOptions(listOf(IGNORING_EXTRA_FIELDS))
      )
    }

    assertThat(Table(dataSource, "joke_request_log", arrayOf(Order.asc("id"))))
      .hasNumberOfRows(1)
      .row(index = 0) {
        value("source").isEqualTo("NORRIS_API")
        value("status").isEqualTo("ERROR")
        value("joke_id").isNull
        value("joke_text").isNull
        value("error_message").isEqualTo("java.lang.IllegalStateException: Norris api returned null")
      }

    wireMock.verify {
      urlPath equalTo "/jokes/random"
      method = GET
      exactly = 1
    }
  }

  @Test
  @DataSet("dataset/joke/joke-request-log.empty.yaml")
  fun `test norris api source when server responds ok with malformed response`(wireMockRI: WireMockRuntimeInfo) {
    val wireMock = wireMockRI.wireMock.apply { stubOkMalformedResponse() }

    Given { port(localServerPort) } When { get("/api/v1/joke/random?source=norris-api") } Then {
      statusCode(500)
      contentType(APPLICATION_JSON)
      body(
        jsonStringEquals(
          // language=JSON
          """
          {
            "message": "#{json-unit.regex}^.+Error while extracting response.+$"
          }
        """
        ).withOptions(listOf(IGNORING_EXTRA_FIELDS))
      )
    }

    assertThat(Table(dataSource, "joke_request_log", arrayOf(Order.asc("id"))))
      .hasNumberOfRows(1)
      .row(index = 0) {
        value("source").isEqualTo("NORRIS_API")
        value("status").isEqualTo("ERROR")
        value("joke_id").isNull
        value("joke_text").isNull
        value("error_message").satisfies(HamcrestCondition(StringContains("Error while extracting response")))
      }

    wireMock.verify {
      urlPath equalTo "/jokes/random"
      method = GET
      exactly = 1
    }
  }

  @Test
  @DataSet("dataset/joke/joke-request-log.empty.yaml")
  fun `test norris api source when server responds not found`(wireMockRI: WireMockRuntimeInfo) {
    val wireMock = wireMockRI.wireMock.apply { stubNotFound() }

    Given { port(localServerPort) } When { get("/api/v1/joke/random?source=norris-api") } Then { statusCode(404) }

    assertThat(Table(dataSource, "joke_request_log", arrayOf(Order.asc("id"))))
      .hasNumberOfRows(1)
      .row(index = 0) {
        value("source").isEqualTo("NORRIS_API")
        value("status").isEqualTo("NOT_FOUND")
        value("joke_id").isNull
        value("joke_text").isNull
        value("error_message").isNull
      }

    wireMock.verify {
      urlPath equalTo "/jokes/random"
      method = GET
      exactly = 1
    }
  }

  @Test
  @DataSet("dataset/joke/joke-request-log.empty.yaml")
  fun `test norris api source when server responds service unavailable`(wireMockRI: WireMockRuntimeInfo) {
    val wireMock = wireMockRI.wireMock.apply { stubServiceUnavailable() }

    Given { port(localServerPort) } When { get("/api/v1/joke/random?source=norris-api") } Then {
      statusCode(500)
      contentType(APPLICATION_JSON)
      body(
        jsonStringEquals(
          // language=JSON
          """
          {
            "message": "#{json-unit.regex}^.+503 Service Unavailable.+$"
          }
        """
        ).withOptions(listOf(IGNORING_EXTRA_FIELDS))
      )
    }

    assertThat(Table(dataSource, "joke_request_log", arrayOf(Order.asc("id"))))
      .hasNumberOfRows(1)
      .row(index = 0) {
        value("source").isEqualTo("NORRIS_API")
        value("status").isEqualTo("ERROR")
        value("joke_id").isNull
        value("joke_text").isNull
        value("error_message").satisfies(HamcrestCondition(StringContains("503 Service Unavailable")))
      }

    wireMock.verify {
      urlPath equalTo "/jokes/random"
      method = GET
      exactly = 1
    }
  }
}
