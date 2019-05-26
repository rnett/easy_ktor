package com.rnett.easy_ktor

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authentication
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.sessions.CurrentSession
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import io.ktor.util.pipeline.PipelineContext
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class EndpointException(val responseCode: HttpStatusCode, message: String) : RuntimeException(message)

class CheckedEndpoint {
    fun respondError(statusCode: HttpStatusCode, message: String = ""): Nothing =
        throw EndpointException(statusCode, message)

    fun respondSuccess(): Nothing =
        throw EndpointException(HttpStatusCode.OK, "")

    suspend inline fun <reified E: Exception, R> handle(responseCode: HttpStatusCode, crossinline body: suspend () -> R) =
        try{
            body()
        } catch (e: Exception){
            if(e is E){
                throw EndpointException(responseCode, e.message ?: "")
            } else
                throw e
        }

    suspend inline fun <reified E: Exception> handleUnit(responseCode: HttpStatusCode, crossinline body: suspend () -> Unit) =
        try{
            body()
        } catch (e: Exception){
            if(e is E){
                throw EndpointException(responseCode, e.message ?: "")
            } else
                throw e
        }
}

suspend inline fun PipelineContext<Unit, ApplicationCall>.checked(
    catchAll: HttpStatusCode? = null,
    crossinline builder: suspend CheckedEndpoint.() -> Unit
) {
    try {
        builder(CheckedEndpoint())
    } catch (ee: EndpointException) {
        call.respond(ee.responseCode, ee.message ?: "")
    } catch (e: Exception) {
        if (catchAll != null) {
            call.respond(catchAll, e.message ?: "")
        } else throw e
    }
}

