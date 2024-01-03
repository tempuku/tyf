package queue.file.modified

import config.QueueConfig
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files

class FileModifiedQueueExecutor(private val config: QueueConfig, private val consumer: FileModifiedConsumer) {

    fun exec() {
        val files = config.directory.listFiles()
        if (files == null) {
            return
        }
        files.map { transform(it) }.forEach { consumer.consume(it) }
    }

    private fun transform(file: File): FileModifiedPayload {
        val bytes = Files.readAllBytes(file.toPath())
        return Json.decodeFromString<FileModifiedPayload>(String(bytes))
    }

}