package tech.stoneapp.secminhr.cavern.articlecontent.markwonUtils

import android.graphics.Color
import android.text.style.ForegroundColorSpan
import ru.noties.markwon.MarkwonConfiguration
import ru.noties.markwon.RenderProps
import ru.noties.markwon.SpanFactory
import ru.noties.markwon.core.CoreProps
import ru.noties.markwon.core.spans.HeadingSpan

class HeadingSpanFactory: SpanFactory {

    override fun getSpans(configuration: MarkwonConfiguration, props: RenderProps): Any? {
        return arrayOf(HeadingSpan(configuration.theme(), CoreProps.HEADING_LEVEL.require(props)),
                ForegroundColorSpan(Color.BLACK))
    }
}