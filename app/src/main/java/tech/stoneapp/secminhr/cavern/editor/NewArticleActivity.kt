package tech.stoneapp.secminhr.cavern.editor

import android.preference.PreferenceManager
import androidx.core.content.edit
import stoneapp.secminhr.cavern.api.Cavern
import stoneapp.secminhr.cavern.cavernError.CavernError
import stoneapp.secminhr.cavern.isNotNullNorEmpty

class NewArticleActivity: EditorActivity() {
    override fun send(title: String?, content: String?, onSuccess: () -> Unit, onFailure: (CavernError) -> Unit) {
        Cavern.getInstance(this)
                .publishArticle(title!!, content!!)
                .addOnSuccessListener {
                    PreferenceManager.getDefaultSharedPreferences(application).edit {
                        putBoolean("article_outdated", true)
                        putString("article_title", "")
                        putString("article_content", "")
                    }
                    onSuccess()
                }
                .addOnFailureListener { onFailure(it) }
                .execute()
    }

    override fun titleCheck(title: String?): Boolean {
        return title.isNotNullNorEmpty()
    }

    override fun contentCheck(content: String?): Boolean {
        return content.isNotNullNorEmpty()
    }

    override fun save(title: String?, content: String?) {
        PreferenceManager.getDefaultSharedPreferences(this).edit {
            putString("article_title", title)
            putString("article_content", content)
        }
    }

    override fun restore(): Pair<String, String> {
        val manager = PreferenceManager.getDefaultSharedPreferences(this)
        var title = manager.getString("article_title", "(Untitled)")
        var content = manager.getString("article_content", "")
        if(title.isNullOrEmpty()) {
            title = "(Untitled)"
        }
        if(content.isNullOrEmpty()) {
            content = ""
        }
        return title to content
    }
}