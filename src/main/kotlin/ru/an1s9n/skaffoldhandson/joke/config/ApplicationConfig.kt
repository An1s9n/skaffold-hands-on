package ru.an1s9n.skaffoldhandson.joke.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import ru.an1s9n.skaffoldhandson.joke.norris.NorrisApiProperties

@Configuration
@EnableConfigurationProperties(NorrisApiProperties::class)
class ApplicationConfig
