package com.raywenderlich.webapp

import io.ktor.application.*
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.locations.*
import io.ktor.locations.get
import io.ktor.response.*
import io.ktor.routing.Route

const val HOME = "/"

@Location(HOME)
class Home

fun Route.home() {
  get<Home> {
    call.respond(FreeMarkerContent("home.ftl", null))
  }
}