package service

import queue.file.modified.FileModifiedPayload
import queue.file.modified.FileModifiedProducer
import repository.file.FileRepository
import repository.track.TrackRepository
import java.time.Instant

class ScanFileUpdatesService(
    private val trackRepository: TrackRepository,
    private val fileRepository: FileRepository,
    private val fileModifiedProducer: FileModifiedProducer
) {
    fun scan() {
        trackRepository.trackedFiles().forEach { file ->
            val currentPath = file.toPath()
            val fileInfo = fileRepository.getByPath(currentPath)

            val lastModifiedMillis = file.lastModified()
            if (lastModifiedMillis > fileInfo.lastScanMillis) {
                fileInfo.lastScanMillis = Instant.now().toEpochMilli()
                fileModifiedProducer.produce(FileModifiedPayload(file.toString(), file.lastModified()))
            }
        }
        fileRepository.syncFs()
    }

}