package com.hoppipotame.discord.domain.services

import com.hoppipotame.discord.domain.model.SearchQuery
import com.hoppipotame.discord.domain.model.Torrent
import com.hoppipotame.discord.domain.port.inBound.DownloadTorrentFileUseCase
import com.hoppipotame.discord.domain.port.inBound.SearchTorrentUseCase
import com.hoppipotame.discord.domain.services.catalog.TorrentCatalog

class TorrentService(private val catalog: TorrentCatalog) : SearchTorrentUseCase, DownloadTorrentFileUseCase {
    override fun searchTorrent(searchQuery: SearchQuery): List<Torrent> =
        catalog.search(searchQuery)

    override fun downloadFromUrl(torrent: Torrent) {
        TODO("Not yet implemented")
    }
}