package ru.an1s9n.skaffoldhandson.joke.requestlog

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementSetter
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Repository
import ru.an1s9n.skaffoldhandson.joke.Joke
import ru.an1s9n.skaffoldhandson.joke.JokeSource
import ru.an1s9n.skaffoldhandson.joke.requestlog.JokeRequestStatus.ERROR
import ru.an1s9n.skaffoldhandson.joke.requestlog.JokeRequestStatus.NEW
import ru.an1s9n.skaffoldhandson.joke.requestlog.JokeRequestStatus.NOT_FOUND
import ru.an1s9n.skaffoldhandson.joke.requestlog.JokeRequestStatus.OK

@Repository
class DbJokeRequestLogRepository(private val jdbcTemplate: JdbcTemplate) : JokeRequestLogRepository {

  override fun saveNewReturningId(source: JokeSource): Long =
    jdbcTemplate.query(
      "INSERT INTO $JOKE_REQUEST_LOG (source, status) VALUES (?, '$NEW') RETURNING id",
      PreparedStatementSetter { ps -> ps.setString(1, "$source") },
      ResultSetExtractor { rs -> rs.next(); rs.getLong("id") }
    )!!

  override fun markOk(id: Long, joke: Joke) {
    jdbcTemplate.update("UPDATE $JOKE_REQUEST_LOG SET status = '$OK', joke_id = ?, joke_text = ? WHERE id = ?") { ps ->
      ps.setString(1, joke.id)
      ps.setString(2, joke.text)
      ps.setLong(3, id)
    }
  }

  override fun markNotFound(id: Long) {
    jdbcTemplate.update("UPDATE $JOKE_REQUEST_LOG SET status = '$NOT_FOUND' WHERE id = ?") { ps ->
      ps.setLong(1, id)
    }
  }

  override fun markError(id: Long, errorMessage: String) {
    jdbcTemplate.update("UPDATE $JOKE_REQUEST_LOG SET status = '$ERROR', error_message = ? WHERE id = ?") { ps ->
      ps.setString(1, errorMessage.take(1000))
      ps.setLong(2, id)
    }
  }
}

private const val JOKE_REQUEST_LOG = "joke_request_log"
