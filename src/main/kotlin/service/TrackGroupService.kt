package service

import config.Configuration
import kotlinx.serialization.json.Json
import model.HistoryChangesModel
import repository.track.TrackRepository
import java.nio.file.Files

class TrackGroupService(
    private val config: Configuration,
    private val trackRepository: TrackRepository,
){
    fun collect() : HistoryChangesModel {
        return trackRepository.trackedFiles()
            .map {config.historyChanges.toPath().resolve(it.toPath().fileName)}
            .map {String(Files.readAllBytes(it))}
            .map {Json.decodeFromString<HistoryChangesModel>(it)}
            .fold (HistoryChangesModel(listOf(), listOf())) { sum, element -> sum + element }
    }


}