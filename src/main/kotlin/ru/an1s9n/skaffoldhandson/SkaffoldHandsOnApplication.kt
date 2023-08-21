package ru.an1s9n.skaffoldhandson

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SkaffoldHandsOnApplication

fun main(args: Array<String>) {
  runApplication<SkaffoldHandsOnApplication>(*args)
}
