package tech.stoneapp.secminhr.cavern

import android.content.Context
import android.os.Build
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.android.volley.VolleyError
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.parser.Parser
import ru.noties.markwon.Markwon
import ru.noties.markwon.SpannableBuilder
import ru.noties.markwon.SpannableConfiguration
import ru.noties.markwon.html.impl.MarkwonHtmlParserImpl
import ru.noties.markwon.il.AsyncDrawableLoader
import ru.noties.markwon.renderer.html2.MarkwonHtmlRenderer
import ru.noties.markwon.spans.SpannableTheme
import ru.noties.markwon.syntax.Prism4jSyntaxHighlight
import ru.noties.markwon.syntax.Prism4jThemeDefault
import ru.noties.markwon.tasklist.TaskListExtension
import ru.noties.prism4j.Prism4j
import tech.stoneapp.secminhr.cavern.api.results.Author
import tech.stoneapp.secminhr.cavern.articlecontent.AtUsernameSpan.AtUsernameProcessor
import tech.stoneapp.secminhr.cavern.articlecontent.AtUsernameSpan.AtUsernameVisitor
import tech.stoneapp.secminhr.cavern.articlecontent.fontTag.FontTagHandler
import tech.stoneapp.secminhr.cavern.articlecontent.markwonUtils.CavernGrammarLocator
import tech.stoneapp.secminhr.cavern.articlecontent.markwonUtils.CavernSpannableFactory

class CavernMarkdownTextView: TextView {

    constructor(context: Context): super(context) {
        initialize()
    }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        initialize()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleArr: Int):
            super(context, attrs, defStyleArr) {
        initialize()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleArr: Int, defStyleRes: Int):
            super(context, attrs, defStyleArr, defStyleRes) {
        initialize()
    }

    private var onUsernameClickedListeners: ((Author) -> Unit)? = null
    private var errorListeners: ((VolleyError) -> Unit)? = null

    fun addOnUsernameClickedListener(listener: (Author) -> Unit): CavernMarkdownTextView {
        onUsernameClickedListeners = listener
        return this
    }

    fun addErrorListener(listener: (VolleyError) -> Unit): CavernMarkdownTextView {
        errorListeners = listener
        return this
    }


    lateinit var theme: SpannableTheme
    lateinit var htmlRenderer: MarkwonHtmlRenderer
    lateinit var config: SpannableConfiguration
    lateinit var parser: Parser

    private fun initialize() {
        theme = createCavernMarkdownTheme()
        htmlRenderer = createCavernHtmlRenderer()
        config = createCavernMarkdownConfig()
        parser = markdownBasicBuilder()
                .customDelimiterProcessor(AtUsernameProcessor())
                .build()
    }

    private fun createCavernMarkdownTheme() =
            SpannableTheme
                    .builderWithDefaults(context)
                    .headingBreakHeight(0)
                    .build()

    private fun createCavernHtmlRenderer() =
            MarkwonHtmlRenderer
                    .builderWithDefaults()
                    .handler("font", FontTagHandler(resources, context.packageName))
                    .build()

    private fun createCavernMarkdownConfig() =
            SpannableConfiguration
                    .builder(context)
                    .theme(theme)
                    .factory(CavernSpannableFactory())
                    .softBreakAddsNewLine(true)
                    .asyncDrawableLoader(AsyncDrawableLoader.create())
                    .htmlParser(MarkwonHtmlParserImpl.create())
                    .htmlRenderer(htmlRenderer)
                    .syntaxHighlight(Prism4jSyntaxHighlight.create(Prism4j(CavernGrammarLocator()), Prism4jThemeDefault.create()))
                    .build()

    private fun markdownBasicBuilder(): Parser.Builder =
            Parser.Builder().extensions(listOf(
                    StrikethroughExtension.create(),
                    TablesExtension.create(),
                    TaskListExtension.create(),
                    AutolinkExtension.create()
            ))

    fun setMarkdown(text: String) {
        val node = parser.parse(text)
        val builder = SpannableBuilder()
        val customVisitor = AtUsernameVisitor(config, context, builder)
        onUsernameClickedListeners?.let {
            customVisitor.addOnSuccessListener(it)
        }
        errorListeners?.let {
            customVisitor.addOnErrorListener(it)
        }
        node.accept(customVisitor)
        val rendered = builder.text()
        this.movementMethod = LinkMovementMethod.getInstance()
        Markwon.unscheduleDrawables(this)
        Markwon.unscheduleTableRows(this)
        this.text = rendered
        Markwon.scheduleDrawables(this)
        Markwon.scheduleTableRows(this)
    }
}