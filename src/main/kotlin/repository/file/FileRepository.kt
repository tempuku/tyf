package repository.file

import config.Configuration
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class FileRepository(private val config: Configuration) {

    private val hashIndex: MutableMap<Path, FileInfo>

    init {
        val fileInfo = loadFileMeta(config)
        hashIndex = fileInfo.content.associateBy { Paths.get(it.path) }.toMutableMap()
    }

    fun getByPath(path: Path): FileInfo {
        return hashIndex.getOrPut(path) { FileInfo(path.toString(), 0) }
    }

    fun syncFs() {
        val fileInfoList = FileInfoList(hashIndex.values.toList())
        val json = Json.encodeToString(fileInfoList)
        Files.write(config.fileInfo.toPath(), json.toByteArray())
    }
}

@Serializable
data class FileInfoList(val content: List<FileInfo>)

@Serializable
data class FileInfo(val path: String, var lastScanMillis: Long)

private fun loadFileMeta(config: Configuration): FileInfoList {
    val bytes = Files.readAllBytes(config.fileInfo.toPath())
    return Json.decodeFromString<FileInfoList>(String(bytes))
}
