package repository.historyChanges

import config.Configuration
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.HistoryChangesModel
import java.nio.file.Files
import kotlin.io.path.Path

class HistoryChangesRepository(private val config: Configuration) {
    fun toFile(historyChanges: HistoryChangesModel) {
        val changesJson = Json.encodeToString(historyChanges)
        println(config.historyChanges.toString())
//        TODO: filePathName need to change to TrackGroupId
        Files.write(Path(config.historyChanges.toString(), historyChanges.filePathList.first().split("/").last()), changesJson.toByteArray())
    }

}