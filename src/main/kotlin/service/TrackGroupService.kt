package service

import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfName
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.annot.PdfAnnotation
import com.itextpdf.kernel.pdf.annot.PdfTextMarkupAnnotation
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import com.itextpdf.kernel.pdf.canvas.parser.filter.TextRegionEventFilter
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredTextEventListener
import config.Annotation
import config.Configuration
import config.Content
import config.TrackList
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.HistoryChangesModel
import model.pdf.PdfTextExtractionStrategy
import repository.historyChanges.HistoryChangesRepository
import repository.track.TrackRepository
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path

class TrackGroupService(
    private val config: Configuration,
    private val trackRepository: TrackRepository,
){
    fun collect() : HistoryChangesModel? {
        return trackRepository.trackedFiles()
            .map {Json.decodeFromString<HistoryChangesModel>(
                String(Files.readAllBytes(config.historyChanges.toPath().resolve(it.toPath().fileName)))
            )}
//            .filter {!it.isEmpty()}
            ?.reduceOrNull { sum, element -> sum + element }
    }


}