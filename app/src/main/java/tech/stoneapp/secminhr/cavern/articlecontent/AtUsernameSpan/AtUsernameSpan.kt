package tech.stoneapp.secminhr.cavern.articlecontent.AtUsernameSpan

import android.content.Context
import android.text.style.URLSpan
import android.view.View
import com.android.volley.VolleyError
import tech.stoneapp.secminhr.cavern.api.Cavern
import tech.stoneapp.secminhr.cavern.api.results.Author

class AtUsernameSpan(val username: String,
                     private val context: Context,
                     successListener: ((Author) -> Unit)? = null,
                     errorListener: ((VolleyError) -> Unit)? = null): URLSpan(username) {

    private val successListeners = mutableListOf<(Author) -> Unit>()
    private val errorListeners = mutableListOf<(VolleyError) -> Unit>()

    init {
        successListener?.let {
            successListeners += it
        }
        errorListener?.let {
            errorListeners += it
        }
    }

    fun addSuccessListener(listener: (Author) -> Unit): AtUsernameSpan {
        successListeners += listener
        return this
    }

    fun addErrorListener(listener: (VolleyError) -> Unit): AtUsernameSpan {
        errorListeners += listener
        return this
    }


    override fun onClick(widget: View) {
        val task = Cavern.getInstance(context).getAuthor(username)
        for (listener in successListeners) {
            task.addOnSuccessListener(listener)
        }
        for (listener in errorListeners) {
            task.addOnFailureListener(listener)
        }
        task.execute()
    }
}