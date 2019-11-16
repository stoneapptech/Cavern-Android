package tech.stoneapp.secminhr.cavern.articlecontent.markwonUtils

import android.content.Context
import android.content.res.Resources
import androidx.core.content.res.ResourcesCompat
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.node.Heading
import org.commonmark.parser.Parser
import ru.noties.markwon.AbstractMarkwonPlugin
import ru.noties.markwon.MarkwonConfiguration
import ru.noties.markwon.MarkwonSpansFactory
import ru.noties.markwon.MarkwonVisitor
import ru.noties.markwon.core.MarkwonTheme
import ru.noties.markwon.html.MarkwonHtmlRenderer
import ru.noties.markwon.image.AsyncDrawableLoader
import ru.noties.markwon.syntax.Prism4jSyntaxHighlight
import ru.noties.markwon.syntax.Prism4jThemeDefault
import ru.noties.prism4j.Prism4j
import stoneapp.secminhr.cavern.collideWith
import tech.stoneapp.secminhr.cavern.R
import tech.stoneapp.secminhr.cavern.articlecontent.AtUsernameSpan.AtUsernameNode
import tech.stoneapp.secminhr.cavern.articlecontent.AtUsernameSpan.AtUsernameProcessor
import tech.stoneapp.secminhr.cavern.articlecontent.AtUsernameSpan.AtUsernameVisitor
import tech.stoneapp.secminhr.cavern.articlecontent.fontTag.FontTagHandler
import java.util.*

class CavernPlugin(val resources: Resources, val context: Context): AbstractMarkwonPlugin() {
    override fun processMarkdown(markdown: String): String {
        var useLatex = false
        var latexRanges: MutableList<IntRange> = mutableListOf()
        //latex
        var regex = Regex("```math((?s:.)*)```")
        var adjusted = regex.replace(markdown) {
            useLatex = true
            latexRanges.plusAssign(it.range)
            "$$\n" + it.groupValues.getOrElse(1) {""}
                    .replace("\r", "")
                    .replace("\n", "")+ "\n$$"
        }
        //soft line break
        regex = Regex("([^\n])\n([^\n])")
        adjusted = regex.replace(adjusted) {
            for(range in latexRanges) {
                if(it.range.collideWith(range)) {
                    return@replace it.value
                }
            }
            it.groupValues.getOrElse(1) {""} + "\n\n" + it.groupValues.getOrElse(2){""}
        }
        return adjusted
    }
    override fun configureParser(builder: Parser.Builder) {
        builder.extensions(Collections.singleton(AutolinkExtension.create()))
        builder.customDelimiterProcessor(AtUsernameProcessor())
    }

    override fun configureTheme(builder: MarkwonTheme.Builder) {
        builder.headingBreakHeight(0)
    }

    override fun configureHtmlRenderer(builder: MarkwonHtmlRenderer.Builder) {
        builder.setHandler("font", FontTagHandler(resources, context.packageName))
    }

    override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
        builder.setFactory(Heading::class.java, HeadingSpanFactory())
    }

    override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
        builder.syntaxHighlight(Prism4jSyntaxHighlight.create(Prism4j(CavernGrammarLocator()),
                Prism4jThemeDefault.create()))
    }

    override fun configureVisitor(builder: MarkwonVisitor.Builder) {
        builder.on(AtUsernameNode::class.java, AtUsernameVisitor(context))
    }

    override fun configureImages(builder: AsyncDrawableLoader.Builder) {
        builder.placeholderDrawableProvider {
            val drawable = ResourcesCompat.getDrawable(resources, R.drawable.loading_image_placeholder, null)
            drawable
        }
    }

}