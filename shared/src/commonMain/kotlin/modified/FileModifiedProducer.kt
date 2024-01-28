package queue.file.modified

import com.oldguy.common.io.File
import config.QueueConfig
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

expect fun calculatePlatformMD5(input: String): String

class FileModifiedProducer(private val config: QueueConfig) {

    fun produce(payload: FileModifiedPayload) {
        val queueItemFileName = "${payload.lastModifiedMillis}_${calculateMD5(payload.path)}"
        val queueItemPath = config.directory.path.toPath().resolve(queueItemFileName)
        if (File(queueItemPath.toString()).exists) {
            return
        }
        FileSystem.SYSTEM.write(queueItemPath) {
            writeUtf8(Json.encodeToString(payload))
        }
    }

    private fun calculateMD5(input: String): String {
        return calculatePlatformMD5(input)
    }
    
}