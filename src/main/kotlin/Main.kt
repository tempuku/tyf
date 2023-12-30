import config.initConfig
import queue.file.modified.FileModifiedConsumer
import queue.file.modified.FileModifiedProducer
import queue.file.modified.FileModifiedQueueExecutor
import queue.file.modified.FileModifiedPayload
import repository.file.FileRepository
import repository.historyChanges.HistoryChangesRepository
import repository.track.TrackRepository
import service.HistoryChangesService
import service.ScanFileModificationsService

import java.nio.file.Paths

fun main() {
    val userHome = System.getProperty("user.home")
    val config = initConfig(userHome)

    val trackRepository = TrackRepository(config)
    val fileRepository = FileRepository(config)
    val historyChangesRepository = HistoryChangesRepository(config)

    val historyChangesService = HistoryChangesService(historyChangesRepository, config)

    val fileModifiedProducer = FileModifiedProducer(config.fileUpdateQueue)
    val fileModifiedConsumer = FileModifiedConsumer(config.fileUpdateQueue, historyChangesService)

    val fileModifiedQueueExecutor = FileModifiedQueueExecutor(config.fileUpdateQueue, fileModifiedConsumer)
    val scanFileModificationsService = ScanFileModificationsService(trackRepository, fileRepository, fileModifiedProducer)

    scanFileModificationsService.scan()
    fileModifiedQueueExecutor.exec()
}
