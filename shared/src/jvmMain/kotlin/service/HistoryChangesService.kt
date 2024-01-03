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
import model.HistoryChangesModel
import model.pdf.PdfTextExtractionStrategy
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import org.apache.pdfbox.tools.imageio.ImageIOUtil
import repository.historyChanges.HistoryChangesRepository
import java.io.File

class HistoryChangesService(
    private val config: Configuration,
    private val repository: HistoryChangesRepository,
){
    fun annotationToMd(annotation: Annotation) :String {
        val temp_head = "# %s\n" +
                "**%s**\\\n" +
                "Type: %s\\\n"
        val temp_img = "[![image](./%s)]\n"
        val temp_text = "text: %s\n"
        var result = temp_head.format(annotation.date, annotation.fileName, annotation.type)
        when (annotation.type){
            PdfName.Highlight.toString() -> {
                result += temp_text.format(annotation.content?.text)
            }
            PdfName.Stamp.toString() -> {
                result += temp_img.format(annotation.content?.image_path)
            }
        }
        return result
    }
    fun toMd(changesModel: HistoryChangesModel) :String {
        return changesModel.changesList
            .map (::annotationToMd)
            .fold("") {sum, element -> sum + element}
    }


    private fun normalizeHighlightedText(highlightedText: String): String {
        return highlightedText.replace("\\s+", " ").replace("[“”]", "\"")
    }


    fun convertPdfToPng (src: String, dest: String) {
        val document: PDDocument = PDDocument.load(File(src))
        val pdfRenderer = PDFRenderer(document)
        for (page in 0 until document.numberOfPages) {
            val bim = pdfRenderer.renderImageWithDPI(page, 300f, ImageType.RGB)
            ImageIOUtil.writeImage(bim, dest, 300)
        }
        File(src).delete()
    }


    private fun extract_text(pdfAnnotation: PdfAnnotation): Content? {
        when (pdfAnnotation.getSubtype()){
            PdfName.Highlight -> {
                val annotation = pdfAnnotation as PdfTextMarkupAnnotation
                val textCoordinates = annotation.getRectangle()
                val highlightedArea = textCoordinates.toRectangle()
                val strategy = PdfTextExtractionStrategy(highlightedArea)
                val textFilter = FilteredTextEventListener(
                    strategy, TextRegionEventFilter(highlightedArea)
                )
                val highlightedText = PdfTextExtractor.getTextFromPage(annotation.getPage(),
                    textFilter)
                return Content(null, normalizeHighlightedText(highlightedText))
            }
            PdfName.Stamp -> {
                val annotation = pdfAnnotation
                val coordinates = annotation.getRectangle()
                val area = coordinates.toRectangle()
                val page = annotation.getPage()
                val queueItemFileName = "/${page.getDocument().getDocumentId()}_${annotation.getDate().getValue()}"
//                TODO: create image service
                val configPathPdf = "${config.image}$queueItemFileName.pdf"
                val configPathPng = "${config.image}$queueItemFileName.png"
                val croppedSinglePageTarget = PdfDocument(PdfWriter(configPathPdf))
                page.getDocument().copyPagesTo(1, 1, croppedSinglePageTarget)
                croppedSinglePageTarget.getPage(1).setCropBox(area)
                croppedSinglePageTarget.close()
                convertPdfToPng(configPathPdf, configPathPng)
                return Content("${config.image.name}$queueItemFileName.png", null)
            }
        }

        return null
    }

    private fun manipulatePdf(src: String): HistoryChangesModel {
        val pdfDoc = PdfDocument(PdfReader(src))
        val pageQt = pdfDoc.getNumberOfPages()
        val pages = (1..pageQt).map{pdfDoc.getPage(it)}
        val annotations = pages.map{Pair(it.getAnnotations(), pdfDoc.getPageNumber(it))}
            .filter{(pdfAnnotations, _) -> !pdfAnnotations.isEmpty()}
            .map{(annotList, page_number) ->
                annotList
                    .map{
                        Annotation(it.getDate().getValue(), extract_text(it), it.getSubtype().toString(), page_number, File(src).name)
                    }}
        pdfDoc.close()
        return HistoryChangesModel(listOf(src), annotations.flatten())
    }

    fun fromFile(path: String): HistoryChangesModel {
        return manipulatePdf(path)
    }

    fun toCache(historyChanges: HistoryChangesModel) {
        repository.toFile(historyChanges)
    }

}