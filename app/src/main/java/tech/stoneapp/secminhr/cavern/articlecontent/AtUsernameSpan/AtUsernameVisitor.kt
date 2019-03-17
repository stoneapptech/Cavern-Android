package tech.stoneapp.secminhr.cavern.articlecontent.AtUsernameSpan

import android.content.Context
import com.android.volley.VolleyError
import org.commonmark.node.CustomNode
import ru.noties.markwon.SpannableBuilder
import ru.noties.markwon.SpannableConfiguration
import ru.noties.markwon.renderer.SpannableMarkdownVisitor
import tech.stoneapp.secminhr.cavern.api.results.Author

class AtUsernameVisitor(val config: SpannableConfiguration,
                        val context: Context,
                        val builder: SpannableBuilder = SpannableBuilder(),
                        onSuccess: ((Author) -> Unit)? = null,
                        errorHandler: ((VolleyError) -> Unit)? = null):
        SpannableMarkdownVisitor(config, builder) {

    val successListeners = mutableListOf<(Author) -> Unit>()
    val errorListeners = mutableListOf<(VolleyError) -> Unit>()

    init {
        onSuccess?.let {
            successListeners += it
        }
        errorHandler?.let {
            errorListeners += it
        }
    }

    fun addOnSuccessListener(listener: (Author) -> Unit): AtUsernameVisitor {
        successListeners += listener
        return this
    }

    fun addOnErrorListener(listener: (VolleyError) -> Unit): AtUsernameVisitor {
        errorListeners += listener
        return this
    }


    override fun visit(customNode: CustomNode?) {
        if(customNode is AtUsernameNode) {
            builder.append("@${customNode.username}")
            val usernameSpan = AtUsernameSpan(customNode.username, context)
            for(listener in successListeners) {
                usernameSpan.addSuccessListener(listener)
            }
            for(listener in errorListeners) {
                usernameSpan.addErrorListener(listener)
            }
            builder.setSpan(usernameSpan, builder.length - customNode.username.length - 1, builder.length)
            builder.append(' ')
        } else {
            super.visit(customNode)
        }
    }
}