package com.raywenderlich

import com.raywenderlich.api.phrase
import com.raywenderlich.model.User
import com.raywenderlich.repository.InMemoryRepository
import com.raywenderlich.webapp.*
import com.ryanharter.ktor.moshi.moshi
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.auth.Authentication
import io.ktor.auth.basic
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.freemarker.FreeMarker
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

  install(DefaultHeaders)

  install(StatusPages) {
    exception<Throwable> { e ->
      call.respondText(e.localizedMessage,
        ContentType.Text.Plain, HttpStatusCode.InternalServerError)
    }
  }

  install(ContentNegotiation) {
    moshi()
  }

  install(FreeMarker) {
    templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
  }

  install(Authentication) {
    basic(name = "auth") {
      realm = "Ktor Server"
      validate { credentials ->
        if (credentials.password == "${credentials.name}123") User(credentials.name) else null
      }
    }
  }

  install(Locations)

  val db = InMemoryRepository()

  routing {
    static("/static") {
      resources("images")
    }

    home()
    about()
    phrases(db)

    // API
    phrase(db)
  }
}

const val API_VERSION = "/api/v1"

suspend fun ApplicationCall.redirect(location: Any) {
  respondRedirect(application.locations.href(location))
}