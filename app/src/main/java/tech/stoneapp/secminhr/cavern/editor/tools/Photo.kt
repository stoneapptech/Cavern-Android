package tech.stoneapp.secminhr.cavern.editor.tools

import android.widget.ImageView
import stoneapp.secminhr.cavern.isNotNullNorEmpty

class Photo(icon: Int): EditorTool(icon) {
    
    var altText: String? = null
    var url: String? = null
    
    override fun handle(imageView: ImageView, oldContent: String, cursorPos: Int): Pair<String, Int> {
        val length = oldContent.length
        val beforeCursorContent = oldContent.substring(0 until cursorPos)
        val afterCursorContent = oldContent.substring(cursorPos until length)
        if(altText.isNotNullNorEmpty() && url.isNotNullNorEmpty()) {
            return "$beforeCursorContent![$altText]($url)$afterCursorContent" to
                    beforeCursorContent.length + 2 + altText!!.length + 2 + url!!.length + 1
        }
        return "$beforeCursorContent$afterCursorContent" to cursorPos
    }

    override fun reset(imageView: ImageView) {
        //don't need this
    }
}