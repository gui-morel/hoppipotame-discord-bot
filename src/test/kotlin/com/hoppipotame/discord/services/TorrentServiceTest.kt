package com.hoppipotame.discord.services

import com.hoppipotame.discord.domain.model.SearchQuery
import com.hoppipotame.discord.domain.model.Torrent
import com.hoppipotame.discord.domain.model.TorrentSource.YIFY
import com.hoppipotame.discord.domain.services.TorrentService
import com.hoppipotame.discord.domain.services.catalog.TorrentCatalog
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class InMemoryTorrentCatalog : TorrentCatalog {
    private val catalog: Map<String, Torrent> = mapOf(
        "harry potter" to Torrent("Harry potter", 0, 0, YIFY),
    )

    override fun search(query: SearchQuery): List<Torrent> = listOfNotNull(catalog[query.query])
}

class TorrentServiceTest {

    private val searchTorrentUserCase = TorrentService(InMemoryTorrentCatalog())

    @Test
    fun `should return a list of torrents matching the query`() {
        val matchingQuery = SearchQuery("harry potter")
        val searchResult = searchTorrentUserCase.searchTorrent(matchingQuery)
        val expectedSearchResult = listOf(Torrent("Harry potter", 0, 0, YIFY))
        assertEquals(expectedSearchResult, searchResult)
    }

    @Test
    fun `should return an empty list if no torrents match the query`() {
        val notMatchingQuery = SearchQuery("le seigneur des anneaux")
        val searchResult = searchTorrentUserCase.searchTorrent(notMatchingQuery)
        assertEquals(emptyList(), searchResult)
    }
}