package com.hoppipotame.discord

import com.hoppipotame.discord.domain.model.SearchQuery
import com.hoppipotame.discord.domain.model.Torrent
import com.hoppipotame.discord.domain.port.inBound.SearchTorrentUseCase
import com.hoppipotame.discord.domain.services.TorrentService
import com.hoppipotame.discord.domain.services.catalog.AggregateSourceCatalog
import com.hoppipotame.discord.infrastructure.adapter.SearchTorrentAdapter
import com.hoppipotame.discord.infrastructure.provider.PirateBayTorrentProvider
import com.hoppipotame.discord.infrastructure.provider.YifyTorrentProvider
import dev.kord.common.entity.ButtonStyle.Secondary
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.reply
import dev.kord.core.entity.interaction.ButtonInteraction
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.builder.message.actionRow
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json

private val httpClient = HttpClient {
    install(Logging)
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
}

private val messageDataMap: MutableMap<Snowflake, Torrent> = mutableMapOf()

private val searchTorrentUserCase: SearchTorrentUseCase = TorrentService(
    catalog = AggregateSourceCatalog(
        searchTorrentPort = SearchTorrentAdapter(
            torrentProviders = listOf(
                YifyTorrentProvider("https://yts.am", httpClient),
                PirateBayTorrentProvider("https://apibay.org", httpClient)
            )
        )
    )
)

suspend fun main() {

    val token = System.getenv("HOPPIPOTAME_DISCORD_TOKEN")
    val kord = Kord(token)

    kord.on<ButtonInteractionCreateEvent> {
        when (interaction.data.data.customId.value) {
            "download_torrent" -> handleDownloadTorrent(interaction)
            else -> TODO()
        }
    }

    kord.on<MessageCreateEvent> {
        if (message.author?.isBot != false) return@on
        val words = message.content.split(" ")
        when (words.firstOrNull()) {
            "!search", "!movie" -> {
                search(words.drop(1).joinToString(" "))
            }
        }
    }

    kord.login {
        @OptIn(PrivilegedIntent::class)
        intents += Intent.MessageContent
    }
}

suspend fun handleDownloadTorrent(buttonInteraction: ButtonInteraction) {
    buttonInteraction.message.channel.getMessage(buttonInteraction.message.id).edit {
        actionRow {
            interactionButton(Secondary, "downloading") {
                disabled = true
                label = "Downloading ⏳"
            }
        }
    }
    buttonInteraction.deferPublicMessageUpdate()
    delay(2000)
    buttonInteraction.message.channel.getMessage(buttonInteraction.message.id).edit {
        actionRow {
            interactionButton(Secondary, "downloaded") {
                disabled = true
                label = "Downloaded ✅"
            }
        }
    }
}

private suspend fun MessageCreateEvent.search(query: String) {
    println("Search $query")
    val searchTorrent = searchTorrentUserCase.searchTorrent(SearchQuery(query))
    if (searchTorrent.isEmpty()) {
        message.reply {
            suppressNotifications = true
            content = "No result \uD83D\uDE1F"
        }
    }
    searchTorrent.forEachIndexed { index, torrent ->
            val messageCreated = message.channel.createMessage {
                suppressNotifications = true
                content = "${index + 1}. ${torrent.name}"
                actionRow {
                    interactionButton(Secondary, "download_torrent") {
                        label = "Get from ${torrent.source.displayName}"
                    }
                }
            }
            messageDataMap[messageCreated.id] = torrent
        }
}