package queue.file.modified

import com.oldguy.common.io.File
import config.QueueConfig
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.Source
import repository.file.FileInfoList


class FileModifiedQueueExecutor(private val config: QueueConfig, private val consumer: FileModifiedConsumer) {

    fun exec() {
        val files = config.directory.listFiles
        if (files.isEmpty()) {
            return
        }
        files.map { transform(it) }.forEach { consumer.consume(it) }
    }

    private fun transform(file: File): FileModifiedPayload {
        val bytes : Source = FileSystem.SYSTEM.source(file.path.toPath())
        return Json.decodeFromString<FileModifiedPayload>(bytes.toString())
    }

}