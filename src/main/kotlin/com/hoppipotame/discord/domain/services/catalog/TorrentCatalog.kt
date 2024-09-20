package com.hoppipotame.discord.domain.services.catalog

import com.hoppipotame.discord.domain.model.SearchQuery
import com.hoppipotame.discord.domain.model.Torrent

interface TorrentCatalog {
    fun search(query: SearchQuery): List<Torrent>
}