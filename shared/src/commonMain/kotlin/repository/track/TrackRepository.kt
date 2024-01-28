package repository.track

import com.oldguy.common.io.File
import config.Configuration
import config.Track
import config.TrackList
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.Source

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
    val bytes : Source = FileSystem.SYSTEM.source(config.tracks.path.toPath())
    return Json.decodeFromString<TrackList>(bytes.toString())
}

fun tracksToFiles(tracks: TrackList): List<File> {
    return tracks.content.flatMap { trackToFiles(it) }.toList()
}

fun trackToFiles(track: Track): List<File> {
    val file = File(track.path)
    if (!file.exists) {
        return listOf()
    }
    val mutableList = mutableListOf<File>()
    if (file.isDirectory) {
        file.listFiles.forEach {
                mutableList.add(it)
        }
    } else {
        mutableList.add(file)
    }
    return mutableList.toList()
}