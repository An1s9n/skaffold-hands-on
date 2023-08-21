package ru.an1s9n.skaffoldhandson.joke.db

import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Repository
import ru.an1s9n.skaffoldhandson.joke.FindJokeResult
import ru.an1s9n.skaffoldhandson.joke.FindJokeResult.Error
import ru.an1s9n.skaffoldhandson.joke.FindJokeResult.NotFound
import ru.an1s9n.skaffoldhandson.joke.FindJokeResult.Ok
import ru.an1s9n.skaffoldhandson.joke.Joke
import ru.an1s9n.skaffoldhandson.joke.JokeRepository
import ru.an1s9n.skaffoldhandson.joke.JokeSource

@Repository
class DbJokeRepository(private val jdbcTemplate: JdbcTemplate) : JokeRepository {

  private val log = LoggerFactory.getLogger(this.javaClass)

  override fun supportedSource(): JokeSource = JokeSource.DB

  override fun findRandomJoke(): FindJokeResult =
    try {
      jdbcTemplate.query(
        "SELECT id, text FROM $JOKE ORDER BY random() limit 1",
        ResultSetExtractor { rs ->
          if (rs.next()) Ok(Joke(id = rs.getLong("id").toString(), text = rs.getString("text"))) else NotFound
        },
      )!!
    } catch (e: Exception) {
      log.warn("findRandomJoke failed: $e")
      Error("$e")
    }
}

private const val JOKE = "joke"
