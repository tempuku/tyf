package service

import config.Configuration
import repository.historyChanges.HistoryChangesRepository

expect class HistoryChangesService (
    config: Configuration,
    repository: HistoryChangesRepository,
){
    val config: Configuration
    val repository: HistoryChangesRepository
}