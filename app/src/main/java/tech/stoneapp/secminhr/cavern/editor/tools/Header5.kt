package tech.stoneapp.secminhr.cavern.editor.tools

import android.widget.ImageView

class Header5(icon: Int): EditorTool(icon) {
    override fun handle(imageView: ImageView, oldContent: String, cursorPos: Int): Pair<String, Int> {
        val length = oldContent.length
        val beforeCursorContent = oldContent.substring(0 until cursorPos)
        val afterCursorContent = oldContent.substring(cursorPos until length)
        var sharps = "##### "
        val frontNewline = beforeCursorContent.isNotEmpty() && beforeCursorContent.last() != '\n'
        val endNewline = afterCursorContent.isNotEmpty() && afterCursorContent.first() != '\n'
        if(frontNewline) {
            sharps = "\n$sharps"
        }
        if(endNewline) {
            sharps += "\n"
        }
        val offset = sharps.trimEnd('\n').length
        return "$beforeCursorContent$sharps$afterCursorContent" to
                beforeCursorContent.length + offset
    }

    override fun reset(imageView: ImageView) {
        //don't need this
    }
}