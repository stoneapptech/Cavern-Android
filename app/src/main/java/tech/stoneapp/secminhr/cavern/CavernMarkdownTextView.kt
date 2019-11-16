package tech.stoneapp.secminhr.cavern

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.util.Log
import ru.noties.markwon.Markwon
import ru.noties.markwon.ext.latex.JLatexMathPlugin
import ru.noties.markwon.ext.strikethrough.StrikethroughPlugin
import ru.noties.markwon.ext.tables.TablePlugin
import ru.noties.markwon.ext.tasklist.TaskListPlugin
import ru.noties.markwon.html.HtmlPlugin
import ru.noties.markwon.image.AsyncDrawableSpan
import ru.noties.markwon.image.ImagesPlugin
import ru.noties.markwon.image.gif.GifPlugin
import ru.noties.markwon.image.okhttp.OkHttpImagesPlugin
import stoneapp.secminhr.cavern.api.results.Author
import stoneapp.secminhr.cavern.cavernError.CavernError
import tech.stoneapp.secminhr.cavern.articlecontent.AtUsernameSpan.AtUsernamePlugin
import tech.stoneapp.secminhr.cavern.articlecontent.AtUsernameSpan.AtUsernameVisitor
import tech.stoneapp.secminhr.cavern.articlecontent.markwonUtils.CavernPlugin

class CavernMarkdownTextView: androidx.appcompat.widget.AppCompatTextView {

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

    private var onUsernameClickedListeners: ((Author) -> Unit)? = null
    private var errorListeners: ((CavernError) -> Unit)? = null

    fun addOnUsernameClickedListener(listener: (Author) -> Unit): CavernMarkdownTextView {
        onUsernameClickedListeners = listener
        return this
    }

    fun addErrorListener(listener: (CavernError) -> Unit): CavernMarkdownTextView {
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
                    .usePlugin(GifPlugin.create(true))
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

    override fun setText(text: CharSequence?, type: BufferType?) {
        Log.e("CavernTextView", text.toString())
        super.setText(text, type)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.e("CavernTextView", (text is Spanned).toString())
        if(text is Spanned) {
            val drawableSpans = (text as Spanned).getSpans(0, text.length -1, AsyncDrawableSpan::class.java)
            Log.e("CavernTextView", drawableSpans.toString())
            val drawables = drawableSpans.map { it.drawable }
            Log.e("CavernTextView", drawables.size.toString())
            for(drawable in drawables) {
                Log.e("CavernTextView", "asyncDrawable")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Log.e("CavernTextView", "animated drawable?:${drawable is AnimatedVectorDrawable}")
                }
            }
        }
    }
}