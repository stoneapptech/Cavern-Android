package tech.stoneapp.secminhr.cavern.editor

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.core.content.edit
import stoneapp.secminhr.cavern.api.Cavern
import stoneapp.secminhr.cavern.cavernError.CavernError
import stoneapp.secminhr.cavern.isNotNullNorEmpty
import kotlin.properties.Delegates

class NewCommentActivity: EditorActivity() {

    private var pid by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pid = intent.getIntExtra("pid", -1)
        if (pid == -1) {
            throw IllegalStateException("extra: pid doesn't exists")
        }
        this.pid = pid
        editorFragment.title.set("Comment")
    }

    override fun send(title: String?, content: String?, onSuccess: () -> Unit, onFailure: (CavernError) -> Unit) {
        Cavern.getInstance(this)
                .sendComment(pid, content!!)
                .addOnSuccessListener {
                    PreferenceManager.getDefaultSharedPreferences(application).edit {
                        putBoolean("comment_outdated", true)
                        putString("comment_content", "")
                    }
                    onSuccess()
                }
                .addOnFailureListener(onFailure)
                .execute()
    }

    override fun titleCheck(title: String?): Boolean {
        return true
    }

    override fun contentCheck(content: String?): Boolean {
        return content.isNotNullNorEmpty()
    }

    override fun save(title: String?, content: String?) {
        PreferenceManager.getDefaultSharedPreferences(this).edit {
            putString("comment_content", content)
        }
    }

    override fun restore(): Pair<String, String> {
        val preference = PreferenceManager.getDefaultSharedPreferences(this)
        val content = preference.getString("comment_content", "")
        return "Comment" to content
    }
}