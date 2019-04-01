package tech.stoneapp.secminhr.cavern.articlecontent.AtUsernameSpan

import android.content.Context
import com.android.volley.VolleyError
import ru.noties.markwon.MarkwonVisitor
import tech.stoneapp.secminhr.cavern.api.results.Author

class AtUsernameVisitor(val context: Context,
                        onSuccess: ((Author) -> Unit)? = null,
                        errorHandler: ((VolleyError) -> Unit)? = null):
        MarkwonVisitor.NodeVisitor<AtUsernameNode> {

    override fun visit(visitor: MarkwonVisitor, node: AtUsernameNode) {
        visitor.builder().append("@${node.username}")
        val usernameSpan = AtUsernameSpan(node.username, context)
        for(listener in successListeners) {
            usernameSpan.addSuccessListener(listener)
        }
        for(listener in errorListeners) {
            usernameSpan.addErrorListener(listener)
        }
        visitor.builder().setSpan(usernameSpan, visitor.builder().length - node.username.length - 1,
                visitor.builder().length)
        visitor.builder().append(' ')
    }

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
}