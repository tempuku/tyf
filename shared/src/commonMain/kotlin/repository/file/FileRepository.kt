package repository.file

import com.oldguy.common.io.File
import config.Configuration
import config.TrackList
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.Source


class FileRepository(private val config: Configuration) {

    private val hashIndex: MutableMap<File, FileInfo>

    init {
        val fileInfo = loadFileMeta(config)
        hashIndex = fileInfo.content.associateBy { File(it.File) }.toMutableMap()
    }

    fun getByPath(file: File): FileInfo {
        return hashIndex.getOrPut(file) { FileInfo(File.toString(), 0) }
    }

    fun syncFs() {
        val fileInfoList = FileInfoList(hashIndex.values.toList())
        val json = Json.encodeToString(fileInfoList)
        FileSystem.SYSTEM.write(config.fileInfo.path.toPath()) {
            writeUtf8(json)
        }
    }
}

@Serializable
data class FileInfoList(val content: List<FileInfo>)

@Serializable
data class FileInfo(val File: String, var lastScanMillis: Long)

private fun loadFileMeta(config: Configuration): FileInfoList {
    val bytes : Source = FileSystem.SYSTEM.source(config.fileInfo.path.toPath())
    return Json.decodeFromString<FileInfoList>(bytes.toString())
}
