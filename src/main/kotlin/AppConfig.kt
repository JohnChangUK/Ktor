import io.ktor.application.*
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.pipeline.PipelineContext
import io.ktor.request.receive
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import kotlinx.html.*
import model.Person
import model.PersonRepo
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.text.DateFormat
import java.time.Duration

const val REST_ENDPOINT = "/person"

fun Application.main() {
    install(DefaultHeaders)
    install(CORS) {
        maxAge = Duration.ofDays(1)
    }
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }
    routing {
        get("$REST_ENDPOINT/{id}") {
            errorAware {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Parameter id not found")
                call.respond(PersonRepo.get(id))
            }
        }
        get(REST_ENDPOINT) {
            errorAware {
                call.respond(PersonRepo.getAll())
            }
        }
        delete("$REST_ENDPOINT/{id}") {
            errorAware {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Parameter id not found")
                call.respondSuccessJson(PersonRepo.remove(id))
            }
        }
        delete(REST_ENDPOINT) {
            errorAware {
                PersonRepo.clear()
                call.respondSuccessJson()
            }
        }
        post(REST_ENDPOINT) {
            errorAware {
                val receive = call.receive<Person>()
                println("Received POST request: $receive")
                call.respond(PersonRepo.add(receive))
            }
        }
        get("/") {
            call.respondHtml {
                head {
                    title("Kotlin API example")
                }
                body {
                    div {
                        h1 {
                            +"Welcome to the Persons API"
                        }
                        p {
                            +"Go to '/person' to use the API"
                        }
                    }
                }
            }
        }
    }
}


private suspend fun <R> PipelineContext<*, ApplicationCall>.errorAware(block: suspend () -> R): R? {
    return try {
        block()
    } catch (e: Exception) {
        call.respondText(
            """{"error":"$e"}"""
            , ContentType.parse("application/json")
            , HttpStatusCode.InternalServerError
        )
        null
    }
}

private suspend fun ApplicationCall.respondSuccessJson(value: Boolean = true) =
    respond("""{"success": "$value)"""")
