/*
 * Copyright (c) 2019 Razeware LLC
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish, 
 * distribute, sublicense, create a derivative work, and/or sell copies of the 
 * Software in any work that is designed, intended, or marketed for pedagogical or 
 * instructional purposes related to programming, coding, application development, 
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works, 
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.webapp

import com.raywenderlich.model.EmojiPhrase
import com.raywenderlich.model.User
import com.raywenderlich.repository.Repository
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post

const val PHRASES = "/phrases"

fun Route.phrases(db: Repository) {
    authenticate("auth") {
        get(PHRASES) {
            val user = call.authentication.principal as User
            val phrases = db.phrases()
            call.respond(FreeMarkerContent("phrases.ftl", mapOf("phrases" to phrases, "displayName" to user.displayName)))
        }

        post(PHRASES) {
            val params = call.receiveParameters()
            val action = params["action"] ?: throw IllegalArgumentException("Missing parameter: action")
            when(action) {
                "delete" -> {
                    val id = params["id"] ?: throw IllegalArgumentException("Missing parameter: id")
                    db.remove(id)
                }
                "add" -> {
                    val emoji = params["emoji"] ?: throw IllegalArgumentException("Missing parameter: emoji")
                    val phrase = params["phrase"] ?: throw IllegalArgumentException("Missing parameter: phrase")
                    db.add(EmojiPhrase(emoji, phrase))
                }
            }
            call.respondRedirect(PHRASES)
        }
    }
}