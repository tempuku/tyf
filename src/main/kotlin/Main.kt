import config.Track
import config.TrackList
import config.initConfig
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.HistoryChangesModel
import queue.file.modified.FileModifiedConsumer
import queue.file.modified.FileModifiedProducer
import queue.file.modified.FileModifiedQueueExecutor
import queue.file.modified.FileModifiedPayload
import repository.file.FileRepository
import repository.historyChanges.HistoryChangesRepository
import repository.track.TrackRepository
import service.HistoryChangesService
import service.ScanFileModificationsService
import service.TrackGroupService
import java.io.File
import java.nio.file.Files
import org.apache.log4j.BasicConfigurator;

import java.nio.file.Paths
import kotlin.io.path.Path

fun main() {
    BasicConfigurator.configure();
    val userHome = Paths.get("").toAbsolutePath().toString()
    val changesJson = Json.encodeToString(TrackList(listOf(Track(Paths.get("").toAbsolutePath().toString()+"/src/test/pdf1.pdf"))))
    Files.write(File("$userHome/.tracker/tracks.json").toPath(), changesJson.toByteArray())

    val config = initConfig(userHome)


    val trackRepository = TrackRepository(config)
    val fileRepository = FileRepository(config)
    val historyChangesRepository = HistoryChangesRepository(config)

    val historyChangesService = HistoryChangesService(config, historyChangesRepository)

    val fileModifiedProducer = FileModifiedProducer(config.fileUpdateQueue)
    val fileModifiedConsumer = FileModifiedConsumer(config.fileUpdateQueue, historyChangesService)

    val fileModifiedQueueExecutor = FileModifiedQueueExecutor(config.fileUpdateQueue, fileModifiedConsumer)
    val scanFileModificationsService = ScanFileModificationsService(trackRepository, fileRepository, fileModifiedProducer)
    val trackGroupService = TrackGroupService(config, trackRepository)

    scanFileModificationsService.scan()
    fileModifiedQueueExecutor.exec()
    val historyChanges = trackGroupService.collect()
    val md = historyChangesService.toMd(historyChanges)
    config.resultDirectory.resolve(File("result.md")).writeText(md)
}
