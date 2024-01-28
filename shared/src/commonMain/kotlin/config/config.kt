package config

import com.oldguy.common.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import repository.file.FileInfoList

data class Configuration(val tracks: File, val fileInfo: File, val fileUpdateQueue: QueueConfig, val image: File, val historyChanges: File, val resultDirectory: File)

data class QueueConfig(val name: String, val directory: File)

@Serializable
data class TrackList(val content: List<Track>)

@Serializable
data class Track(val path: String)

@Serializable
data class Content(val image_path: String?, val text: String?)

@Serializable
data class Annotation(val date: String, val content: Content?, val type: String, val page_number: Int, val fileName: String)

suspend fun initConfig(userHome: String): Configuration {
    val root = File("$userHome/.tracker")

    if (!root.exists) {
        val created = root.makeDirectory()
        if (!created) {
            throw RuntimeException("unable to create root directory $root")
        }
    }

    val tracks = File("${root.path}/tracks.json")
    println(tracks.path)
    if (!tracks.exists) {
        val emptyTrackList = TrackList(listOf())
        val json = Json.encodeToString(emptyTrackList)
        FileSystem.SYSTEM.write(tracks.path.toPath()) {
            writeUtf8(json)
        }
    }

    val fileInfoList = File("${root.path}/fileInfo.json")
    if (!fileInfoList.exists) {
        val emptyFileInfoList = FileInfoList(listOf())
        val json = Json.encodeToString(emptyFileInfoList)
        FileSystem.SYSTEM.write(fileInfoList.path.toPath()) {
            writeUtf8(json)
        }
    }

    val queueDirectory = File("${root.path}/queue")
    if (!queueDirectory.exists) {
        val created = queueDirectory.makeDirectory()
        if (!created) {
            throw RuntimeException("unable to create queue directory $queueDirectory")
        }
    }

    val fileModifiedQueueDirectory = File("${root.path}/queue/file-modified")
    if (!fileModifiedQueueDirectory.exists) {
        val created = fileModifiedQueueDirectory.makeDirectory()
        if (!created) {
            throw RuntimeException("unable to create file-modified queue directory $fileModifiedQueueDirectory")
        }
    }

    val storeImageDirectory = File("${root.path}/store")
    if (!storeImageDirectory.exists) {
        val created = storeImageDirectory.makeDirectory()
        if (!created) {
            throw RuntimeException("unable to create image directory $storeImageDirectory")
        }
    }

    val resultImageDirectory = File("${root.path}/store/result")
    if (!resultImageDirectory.exists) {
        val created = resultImageDirectory.makeDirectory()
        if (!created) {
            throw RuntimeException("unable to create image directory $resultImageDirectory")
        }
    }

    val historyChangesDirectory = File("${root.path}/store/changes")
    if (!historyChangesDirectory.exists) {
        val created = historyChangesDirectory.makeDirectory()
        if (!created) {
            throw RuntimeException("unable to create image directory $historyChangesDirectory")
        }
    }

    val fileImageDirectory = File("${root.path}/store/result/image")
    if (!fileImageDirectory.exists) {
        val created = fileImageDirectory.makeDirectory()
        if (!created) {
            throw RuntimeException("unable to create image directory $fileImageDirectory")
        }
    }

    return Configuration(tracks, fileInfoList, QueueConfig("file-modified", fileModifiedQueueDirectory), fileImageDirectory, historyChangesDirectory, resultImageDirectory)
}


