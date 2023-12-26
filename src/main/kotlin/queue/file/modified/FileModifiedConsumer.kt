package queue.file.modified

import config.QueueConfig

class FileModifiedConsumer(private val config: QueueConfig) {

    fun consume(payload: FileModifiedPayload) {
        // надо принимать файлы в обработку
    }

}