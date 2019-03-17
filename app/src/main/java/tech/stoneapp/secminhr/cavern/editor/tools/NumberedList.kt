package tech.stoneapp.secminhr.cavern.editor.tools

import android.widget.ImageView
import tech.stoneapp.secminhr.cavern.R

class NumberedList(icon: Int): EditorTool(icon) {
    
    override fun handle(imageView: ImageView, oldContent: String, cursorPos: Int): Pair<String, Int> {
        val length = oldContent.length
        val beforeCursorContent = oldContent.substring(0 until cursorPos)
        val afterCursorContent = oldContent.substring(cursorPos until length)
        imageView.setBackgroundResource(R.color.colorPrimaryDarker)
        var symbol = "1. "
        val frontNewline = beforeCursorContent.isNotEmpty() && beforeCursorContent.last() != '\n'
        val endNewline = afterCursorContent.isNotEmpty() && afterCursorContent.first() != '\n'
        if(frontNewline) {
            symbol = "\n$symbol"
        }
        if(endNewline) {
            symbol += "\n"
        }
        val offset = symbol.trimEnd('\n').length

        return "$beforeCursorContent$symbol$afterCursorContent" to
                beforeCursorContent.length + offset
    }

    override fun reset(imageView: ImageView) {
        imageView.setBackgroundResource(R.color.colorPrimaryDark)
    }
}