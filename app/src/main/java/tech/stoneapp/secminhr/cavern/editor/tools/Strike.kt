package tech.stoneapp.secminhr.cavern.editor.tools

import android.widget.ImageView

class Strike(icon: Int): EditorTool(icon) {
    override fun handle(imageView: ImageView, oldContent: String, cursorPos: Int): Pair<String, Int> {
        val styleSymbol = "~~~~"
        val length = oldContent.length
        val beforeCursorContent = oldContent.substring(0 until cursorPos)
        val afterCursorContent = oldContent.substring(cursorPos until length)
        return "$beforeCursorContent$styleSymbol$afterCursorContent" to
                beforeCursorContent.length + styleSymbol.length / 2
    }

    override fun reset(imageView: ImageView) {
        //don't need this
    }
}