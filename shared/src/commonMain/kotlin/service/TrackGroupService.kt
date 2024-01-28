package service

import config.Configuration
import kotlinx.serialization.json.Json
import model.HistoryChangesModel
import okio.FileSystem
import okio.Path.Companion.toPath
import repository.track.TrackRepository
import java.nio.file.Files

class TrackGroupService(
    private val config: Configuration,
    private val trackRepository: TrackRepository,
){
    fun collect() : HistoryChangesModel {
        return trackRepository.trackedFiles()
            .map {config.historyChanges.path.toPath().resolve(it.name)}
            .map { FileSystem.SYSTEM.source(it).toString()}
            .map {Json.decodeFromString<HistoryChangesModel>(it)}
            .fold (HistoryChangesModel(listOf(), listOf())) { sum, element -> sum + element }
    }


}