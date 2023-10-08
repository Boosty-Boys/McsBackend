package com.boostyboys.mcs.data.player

import com.boostyboys.mcs.either.Either
import com.boostyboys.mcs.model.player.Player
import com.boostyboys.mcs.model.response.ErrorMessage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class PlayerRepository(private val client: HttpClient) {

    suspend fun getPlayersOnTeamForDate(
        teamId: String,
        date: String,
    ): Either<List<Player>, ErrorMessage> {
        val localDateTime = Instant.parse(date).toLocalDateTime(TimeZone.UTC)
        val formattedDate = "${localDateTime.monthNumber}-${localDateTime.dayOfMonth}-${localDateTime.year}"

        val playersResponse: HttpResponse = client.get {
            this.url("players?on_teams_date=$formattedDate&on_teams_team_ids=$teamId")
        }.body()

        return if (playersResponse.status.isSuccess()) {
            val body = playersResponse.body<List<Player>>()
            Either.success(body)
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
