package model

import config.Annotation
import kotlinx.serialization.Serializable
@Serializable
class HistoryChangesModel(val filePathList: List<String>, val changesList: List<Annotation>) {
    private fun addToChangesList(changesList: MutableList<Annotation>, annotation: Annotation) :MutableList<Annotation> {
        val comparator: Comparator<Annotation> = compareBy(Annotation::date)
        val searchResult = changesList.binarySearch(annotation, comparator)
        val insertionPoint =
            if (searchResult < 0) {
                -(searchResult + 1)
            } else {
                searchResult
            }
        changesList.add(insertionPoint, annotation)
        return changesList
    }
    operator fun plus(historyChangesModel: HistoryChangesModel) : HistoryChangesModel {
        val newFilePath = this.filePathList + historyChangesModel.filePathList
        val copyChangesList = historyChangesModel.changesList.toMutableList()
        this.changesList.forEach{ addToChangesList(copyChangesList, it) }
        return HistoryChangesModel(newFilePath, copyChangesList)
    }
}