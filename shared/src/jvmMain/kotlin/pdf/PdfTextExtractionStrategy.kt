package model.pdf

import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.canvas.parser.EventType
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo
import com.itextpdf.kernel.pdf.canvas.parser.listener.CharacterRenderInfo
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy

/**
 * Pdf text extraction strategy, which cuts the text chunks crossing the extraction area.
 * By default, IText library does not cut such text snippets, so we do it here.
 */
class PdfTextExtractionStrategy(extractionArea: Rectangle) : LocationTextExtractionStrategy() {
    protected var extractionArea: Rectangle

    init {
        this.extractionArea = extractionArea
    }

    @Override
    override fun eventOccurred(eventData: IEventData, eventType: EventType) {
        if (EventType.RENDER_TEXT === eventType) {
            val data: TextRenderInfo = eventData as TextRenderInfo
            // Split the text snippet to chars.
            for (renderInfo in data.getCharacterRenderInfos()) {
                // Get the char rendering boundaries.
                val charArea: Rectangle = CharacterRenderInfo(renderInfo).getBoundingBox()
                if (isInsideExtractionArea(charArea)) {
                    // Extract this char.
                    super.eventOccurred(renderInfo, eventType)
                }
            } //
        }
    }

    /**
     * Check if the rendered text intersects the extraction area.
     * @param textArea Text rendering area.
     * @return True if the text is inside.
     */
    protected fun isInsideExtractionArea(textArea: Rectangle?): Boolean {
        return extractionArea.contains(textArea)
    }
}