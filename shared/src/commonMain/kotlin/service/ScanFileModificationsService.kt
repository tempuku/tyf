package service

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import okio.Path
import queue.file.modified.FileModifiedPayload
import queue.file.modified.FileModifiedProducer
import repository.file.FileRepository
import repository.track.TrackRepository

class ScanFileModificationsService(
    private val trackRepository: TrackRepository,
    private val fileRepository: FileRepository,
    private val fileModifiedProducer: FileModifiedProducer
) {
    fun scan() {
        trackRepository.trackedFiles().forEach { file ->
            val currentPath = file
            val fileInfo = fileRepository.getByPath(currentPath)
            val lastModifiedMillis = file.lastModified.toInstant(TimeZone.of(TimeZone.currentSystemDefault()
                .toString())).epochSeconds
            if (lastModifiedMillis > fileInfo.lastScanMillis) {
                fileInfo.lastScanMillis = Clock.System.now().epochSeconds
                fileModifiedProducer.produce(FileModifiedPayload(file.toString(), file.lastModified.toInstant(TimeZone.of(TimeZone.currentSystemDefault()
                    .toString())).epochSeconds))
            }
        }
        fileRepository.syncFs()
    }

}