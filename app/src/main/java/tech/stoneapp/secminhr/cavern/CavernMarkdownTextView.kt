package tech.stoneapp.secminhr.cavern

import android.content.Context
import android.os.Build
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.android.volley.VolleyError
import ru.noties.markwon.Markwon
import ru.noties.markwon.ext.latex.JLatexMathPlugin
import ru.noties.markwon.ext.strikethrough.StrikethroughPlugin
import ru.noties.markwon.ext.tables.TablePlugin
import ru.noties.markwon.ext.tasklist.TaskListPlugin
import ru.noties.markwon.html.HtmlPlugin
import ru.noties.markwon.image.ImagesPlugin
import ru.noties.markwon.image.okhttp.OkHttpImagesPlugin
import tech.stoneapp.secminhr.cavern.api.results.Author
import tech.stoneapp.secminhr.cavern.articlecontent.AtUsernameSpan.AtUsernamePlugin
import tech.stoneapp.secminhr.cavern.articlecontent.AtUsernameSpan.AtUsernameVisitor
import tech.stoneapp.secminhr.cavern.articlecontent.markwonUtils.CavernPlugin

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


    lateinit var parser: Markwon.Builder

    private fun initialize() {
        parser = markdownBasicBuilder()
    }

    private fun markdownBasicBuilder(): Markwon.Builder =
            Markwon.builder(context)
                    .usePlugin(StrikethroughPlugin.create())
                    .usePlugin(TablePlugin.create(context))
                    .usePlugin(TaskListPlugin.create(context))
                    .usePlugin(HtmlPlugin.create())
                    .usePlugin(ImagesPlugin.create(context))
                    .usePlugin(OkHttpImagesPlugin.create())
                    .usePlugin(JLatexMathPlugin.create(70f))
                    .usePlugin(CavernPlugin(resources, context))

    fun setMarkdown(text: String) {
        if(!parser.build().hasPlugin(AtUsernamePlugin::class.java)) {
            val customVisitor = AtUsernameVisitor(context)
            onUsernameClickedListeners?.let {
                customVisitor.addOnSuccessListener(it)
            }
            errorListeners?.let {
                customVisitor.addOnErrorListener(it)
            }
            parser.usePlugin(AtUsernamePlugin(customVisitor))
        }
        this.movementMethod = LinkMovementMethod.getInstance()
        parser.build().setMarkdown(this, text)
    }
}