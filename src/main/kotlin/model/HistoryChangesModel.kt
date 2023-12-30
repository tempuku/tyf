package model

import config.Annotation
import java.io.File

class HistoryChangesModel(val filePath: File, val changesList: List<Annotation>) {
}