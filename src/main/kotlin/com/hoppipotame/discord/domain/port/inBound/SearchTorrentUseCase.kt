package com.hoppipotame.discord.domain.port.inBound

import com.hoppipotame.discord.domain.model.SearchQuery
import com.hoppipotame.discord.domain.model.Torrent

interface SearchTorrentUseCase {
    fun searchTorrent(searchQuery: SearchQuery): List<Torrent>
}