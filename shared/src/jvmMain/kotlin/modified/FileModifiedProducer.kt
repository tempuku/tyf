package queue.file.modified

import config.QueueConfig
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.security.MessageDigest

class FileModifiedProducer(private val config: QueueConfig) {

    fun produce(payload: FileModifiedPayload) {
        val queueItemFileName = "${payload.lastModifiedMillis}_${calculateMD5(payload.path)}"
        val queueItemPath = config.directory.toPath().resolve(queueItemFileName)
        if (queueItemPath.toFile().exists()) {
            return
        }
        Files.write(queueItemPath, Json.encodeToString(payload).toByteArray())
    }

    private fun calculateMD5(input: String): String {
        val bytes = MessageDigest.getInstance("MD5").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
    
}