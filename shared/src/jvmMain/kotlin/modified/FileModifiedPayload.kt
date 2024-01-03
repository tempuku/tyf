package queue.file.modified

import kotlinx.serialization.Serializable

@Serializable
class FileModifiedPayload(val path: String, val lastModifiedMillis: Long) {
}