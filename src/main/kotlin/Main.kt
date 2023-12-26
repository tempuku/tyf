import config.initConfig
import queue.file.modified.FileModifiedConsumer
import queue.file.modified.FileModifiedProducer
import queue.file.modified.FileModifiedQueueExecutor
import repository.file.FileRepository
import repository.track.TrackRepository
import service.ScanFileModificationsService

fun main() {
    val config = initConfig()
    val trackRepository = TrackRepository(config)
    val fileRepository = FileRepository(config)
    val fileModifiedProducer = FileModifiedProducer(config.fileUpdateQueue)
    val fileModifiedConsumer = FileModifiedConsumer(config.fileUpdateQueue)
    val fileModifiedQueueExecutor = FileModifiedQueueExecutor(config.fileUpdateQueue, fileModifiedConsumer)
    val scanFileModificationsService = ScanFileModificationsService(trackRepository, fileRepository, fileModifiedProducer)

    scanFileModificationsService.scan()
    fileModifiedQueueExecutor.exec()
}
