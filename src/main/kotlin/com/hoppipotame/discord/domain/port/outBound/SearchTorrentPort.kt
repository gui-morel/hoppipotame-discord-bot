package com.hoppipotame.discord.domain.port.outBound

import com.hoppipotame.discord.domain.model.SearchQuery
import com.hoppipotame.discord.domain.model.Torrent
import com.hoppipotame.discord.domain.model.TorrentSource

interface SearchTorrentPort {
    fun searchTorrent(searchQuery: SearchQuery, source: TorrentSource): List<Torrent>
}