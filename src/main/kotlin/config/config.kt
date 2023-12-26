package config

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import repository.file.FileInfoList
import java.io.File
import java.nio.file.Files

data class Configuration(val tracks: File, val storage: File, val fileUpdateQueue: QueueConfig)

data class QueueConfig(val name: String, val directory: File)

@Serializable
data class TrackList(val content: List<Track>)

@Serializable
data class Track(val path: String)

fun initConfig(): Configuration {
    val userHome = System.getProperty("user.home")

    val root = File("$userHome/.tracker")
    if (!root.exists()) {
        val created = root.mkdirs()
        if (!created) {
            throw RuntimeException("unable to create root directory $root")
        }
    }

    val tracks = File("$root/tracks.json")
    if (!tracks.exists()) {
        val emptyTrackList = TrackList(listOf())
        val json = Json.encodeToString(emptyTrackList)
        Files.write(tracks.toPath(), json.toByteArray())
    }

    val fileInfoList = File("$root/fileInfo.json")
    if (!fileInfoList.exists()) {
        val emptyFileInfoList = FileInfoList(listOf())
        val json = Json.encodeToString(emptyFileInfoList)
        Files.write(fileInfoList.toPath(), json.toByteArray())
    }

    val queueDirectory = File("$root/queue")
    if (!queueDirectory.exists()) {
        val created = queueDirectory.mkdirs()
        if (!created) {
            throw RuntimeException("unable to create queue directory $queueDirectory")
        }
    }

    val fileUpdateQueueDirectory = File("$root/queue/file-update")
    if (!fileUpdateQueueDirectory.exists()) {
        val created = fileUpdateQueueDirectory.mkdirs()
        if (!created) {
            throw RuntimeException("unable to create file-update queue directory $fileUpdateQueueDirectory")
        }
    }

    return Configuration(tracks, fileInfoList, QueueConfig("file-update", fileUpdateQueueDirectory))
}


