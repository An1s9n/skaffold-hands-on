package ru.an1s9n.skaffoldhandson.joke

import org.springframework.stereotype.Service
import ru.an1s9n.skaffoldhandson.joke.FindJokeResult.Error
import ru.an1s9n.skaffoldhandson.joke.FindJokeResult.NotFound
import ru.an1s9n.skaffoldhandson.joke.FindJokeResult.Ok
import ru.an1s9n.skaffoldhandson.joke.requestlog.JokeRequestLogRepository

@Service
class JokeService(
  jokeRepos: List<JokeRepository>,
  private val requestLogRepo: JokeRequestLogRepository,
) {

  private val source2Repo: Map<JokeSource, JokeRepository> = jokeRepos.associateBy { it.supportedSource() }

  fun findRandomJoke(source: JokeSource): FindJokeResult {
    val requestLogId = requestLogRepo.saveNewReturningId(source)
    val findJokeResult = source2Repo[source]?.findRandomJoke() ?: error("no JokeRepository exists for source $source")
    when (findJokeResult) {
      is Ok -> requestLogRepo.markOk(requestLogId, findJokeResult.joke)
      is NotFound -> requestLogRepo.markNotFound(requestLogId)
      is Error -> requestLogRepo.markError(requestLogId, findJokeResult.message)
    }
    return findJokeResult
  }
}
