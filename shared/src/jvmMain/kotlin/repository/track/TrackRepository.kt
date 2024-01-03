package repository.track

import config.Configuration
import config.Track
import config.TrackList
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files

class TrackRepository(private val config: Configuration) {

    private val trackedFiles: List<File>

    init {
        val tracks = loadTracks(config)
        trackedFiles = tracksToFiles(tracks)
    }

    fun trackedFiles(): List<File> {
        return trackedFiles
    }

}

fun loadTracks(config: Configuration): TrackList {
    val bytes = Files.readAllBytes(config.tracks.toPath())
    return Json.decodeFromString<TrackList>(String(bytes))
}

fun tracksToFiles(tracks: TrackList): List<File> {
    return tracks.content.flatMap { trackToFiles(it) }.toList()
}

fun trackToFiles(track: Track): List<File> {
    val file = File(track.path)
    if (!file.exists()) {
        return listOf()
    }
    val mutableList = mutableListOf<File>()
    if (file.isDirectory) {
        file.walk().forEach {
            if (it.isFile) {
                mutableList.add(it)
            }
        }
    } else {
        mutableList.add(file)
    }
    return mutableList.toList()
}