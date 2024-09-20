package com.hoppipotame.discord.domain.port.inBound

import com.hoppipotame.discord.domain.model.Torrent

interface DownloadTorrentFileUseCase {
    fun downloadFromUrl(torrent: Torrent)
}