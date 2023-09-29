package com.boostyboys.mcs.data.player

import com.boostyboys.mcs.either.Either
import com.boostyboys.mcs.model.remote.player.PlayerListResponseItem
import com.boostyboys.mcs.model.remote.response.ErrorMessage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess

class PlayerRepository(private val client: HttpClient) {

    suspend fun getAllPlayers(): Either<List<PlayerListResponseItem>, ErrorMessage> {
        val playersResponse: HttpResponse = client.get {
            this.url("players")
        }.body()

        return if (playersResponse.status.isSuccess()) {
            val body = playersResponse.body<List<PlayerListResponseItem>>()

            Either.success(
                body,
            )
        } else {
            Either.failure(
                ErrorMessage(
                    httpStatusCode = playersResponse.status.value,
                    message = playersResponse.status.description,
                ),
            )
        }
    }
}
