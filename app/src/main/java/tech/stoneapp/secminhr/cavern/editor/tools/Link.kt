package tech.stoneapp.secminhr.cavern.editor.tools

import android.util.Log
import android.widget.ImageView
import tech.stoneapp.secminhr.cavern.isNotNullNorEmpty

class Link(icon: Int): EditorTool(icon) {
    var title: String? = null
    var url: String? = null
    override fun handle(imageView: ImageView, oldContent: String, cursorPos: Int): Pair<String, Int> {
        val length = oldContent.length
        val beforeCursorContent = oldContent.substring(0 until cursorPos)
        val afterCursorContent = oldContent.substring(cursorPos until length)
        if(title.isNotNullNorEmpty() && url.isNotNullNorEmpty()) {
            Log.e("Link", "title and url exists")
            return "$beforeCursorContent[$title]($url)$afterCursorContent" to
                    beforeCursorContent.length + 1 + title!!.length + 2 + url!!.length + 1
        }
        return "$beforeCursorContent$afterCursorContent" to cursorPos
    }

    override fun reset(imageView: ImageView) {
        //don't need this
    }
}