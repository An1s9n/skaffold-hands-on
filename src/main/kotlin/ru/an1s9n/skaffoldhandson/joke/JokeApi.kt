package ru.an1s9n.skaffoldhandson.joke

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.server.ResponseStatusException
import ru.an1s9n.skaffoldhandson.joke.FindJokeResult.Error
import ru.an1s9n.skaffoldhandson.joke.FindJokeResult.NotFound
import ru.an1s9n.skaffoldhandson.joke.FindJokeResult.Ok

@Controller
@RequestMapping("/api/v1/joke")
class JokeApi(private val service: JokeService) {

  @GetMapping("/random")
  fun findRandomJoke(source: JokeSource): ResponseEntity<Joke> =
    when (val findJokeResult = service.findRandomJoke(source)) {
      is Ok -> ResponseEntity.ok(findJokeResult.joke)
      is NotFound -> ResponseEntity.notFound().build()
      is Error -> throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, findJokeResult.message)
    }
}
