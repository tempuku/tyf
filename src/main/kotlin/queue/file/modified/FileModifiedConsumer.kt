package queue.file.modified

import config.Annotation
import config.Content

import config.QueueConfig
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfName;
import model.pdf.PdfTextExtractionStrategy;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextMarkupAnnotation;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredTextEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.filter.TextRegionEventFilter;


class FileModifiedConsumer(private val config: QueueConfig) {
    fun normalizeHighlightedText(highlightedText: String): String {
        return highlightedText.replace("\\s+", " ").replace("[“”]", "\"");
    }

    fun extract_text(pdfAnnotation: PdfAnnotation): Content? {
        when (pdfAnnotation.getSubtype()){ 
            PdfName.Highlight -> {
                val annotation = pdfAnnotation as PdfTextMarkupAnnotation
                val textCoordinates = annotation.getRectangle();
                val highlightedArea = textCoordinates.toRectangle();
                val strategy = PdfTextExtractionStrategy(highlightedArea);
                val textFilter = FilteredTextEventListener(
                    strategy, TextRegionEventFilter(highlightedArea));
                val highlightedText = PdfTextExtractor.getTextFromPage(annotation.getPage(),
                    textFilter);
                return Content(null, normalizeHighlightedText(highlightedText));
            }
            PdfName.Stamp -> {
                val annotation = pdfAnnotation
                val coordinates = annotation.getRectangle();
                val area = coordinates.toRectangle();
                val page = annotation.getPage()
                val queueItemFileName = "/${page.getDocument().getDocumentId()}_${annotation.getDate().getValue()}"
                val config_path = config.image_directory.toString() + queueItemFileName + ".pdf"
                val croppedSinglePageTarget = PdfDocument(PdfWriter(config_path.toString()));
                // page.copyTo(croppedSinglePageTarget)
                page.getDocument().copyPagesTo(1, 1, croppedSinglePageTarget)
                croppedSinglePageTarget.getPage(1).setCropBox(area);
                croppedSinglePageTarget.close();
                return Content(config_path.toString(), null);
            }
        }

        return null
    }
    
    fun manipulatePdf(src: String, dest: String): List<Annotation> {
        val pdfDoc = PdfDocument(PdfReader(src));
        val page_qt = pdfDoc.getNumberOfPages()
        val pages = (1..page_qt).map{pdfDoc.getPage(it)}
        val annotations = pages.map{Pair(it.getAnnotations(), pdfDoc.getPageNumber(it))}
        .filter{(page_annots, _) -> !page_annots.isEmpty()}
        .map{(annot_list, page_number) -> 
            annot_list
            .map{Annotation(it.getDate().getValue(), extract_text(it), it.getSubtype().toString(), page_number)
        }}
        System.out.println(annotations)
        pdfDoc.close()
        return annotations.flatten()
    }    

    fun consume(payload: FileModifiedPayload): List<Annotation> {
        return manipulatePdf(payload.path, payload.path+'f')
    }

}