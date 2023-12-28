package config

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import repository.file.FileInfoList
import java.io.File
import java.nio.file.Files

data class Configuration(val tracks: File, val fileInfo: File, val fileUpdateQueue: QueueConfig)

data class QueueConfig(val name: String, val directory: File, val image_directory: File)

@Serializable
data class TrackList(val content: List<Track>)

@Serializable
data class Track(val path: String)

data class Content(val image_path: String?, val text: String?)

data class Annotation(val date: String, val content: Content?, val type: String, val page_number: Int)

fun initConfig(userHome: String): Configuration {
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

    val fileModifiedQueueDirectory = File("$root/queue/file-modified")
    if (!fileModifiedQueueDirectory.exists()) {
        val created = fileModifiedQueueDirectory.mkdirs()
        if (!created) {
            throw RuntimeException("unable to create file-modified queue directory $fileModifiedQueueDirectory")
        }
    }

    val fileImageDirectory = File("$root/image")
    if (!fileImageDirectory.exists()) {
        val created = fileImageDirectory.mkdirs()
        if (!created) {
            throw RuntimeException("unable to create image directory $fileImageDirectory")
        }
    }

    System.out.println(fileImageDirectory)
    return Configuration(tracks, fileInfoList, QueueConfig("file-modified", fileModifiedQueueDirectory, fileImageDirectory))
}


