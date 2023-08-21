package ru.an1s9n.skaffoldhandson.joke.norris

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.DefaultUriBuilderFactory
import ru.an1s9n.skaffoldhandson.joke.FindJokeResult
import ru.an1s9n.skaffoldhandson.joke.FindJokeResult.Error
import ru.an1s9n.skaffoldhandson.joke.FindJokeResult.NotFound
import ru.an1s9n.skaffoldhandson.joke.FindJokeResult.Ok
import ru.an1s9n.skaffoldhandson.joke.Joke
import ru.an1s9n.skaffoldhandson.joke.JokeRepository
import ru.an1s9n.skaffoldhandson.joke.JokeSource

@Component
class NorrisApiJokeRepository(props: NorrisApiProperties) : JokeRepository {

  private val log = LoggerFactory.getLogger(this.javaClass)

  private val restTemplate: RestTemplate = RestTemplate().apply {
    uriTemplateHandler = DefaultUriBuilderFactory(props.baseUrl)
    requestFactory = SimpleClientHttpRequestFactory().apply {
      setConnectTimeout(props.connectTimeoutMillis)
      setReadTimeout(props.readTimeoutMillis)
      messageConverters = listOf(
        MappingJackson2HttpMessageConverter(
          ObjectMapper().findAndRegisterModules()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        )
      )
    }
  }

  override fun supportedSource(): JokeSource = JokeSource.NORRIS_API

  override fun findRandomJoke(): FindJokeResult =
    try {
      val norrisJoke = restTemplate.getForObject("/jokes/random", NorrisJoke::class.java)
      checkNotNull(norrisJoke) { "Norris api returned null" }
      Ok(Joke(id = norrisJoke.id, text = norrisJoke.value))
    } catch (notFoundE: HttpClientErrorException.NotFound) {
      NotFound
    } catch (e: Exception) {
      log.warn("findRandomJoke failed: $e")
      Error("$e")
    }

  private data class NorrisJoke(val id: String, val value: String)
}
