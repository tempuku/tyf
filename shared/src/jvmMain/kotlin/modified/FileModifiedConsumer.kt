package queue.file.modified

import config.QueueConfig
import service.HistoryChangesService


class FileModifiedConsumer(
    private val config: QueueConfig,
    private val historyChangesService: HistoryChangesService
    ) {
    fun consume(payload: FileModifiedPayload){
        historyChangesService.toCache(historyChangesService.fromFile(payload.path))
    }
}