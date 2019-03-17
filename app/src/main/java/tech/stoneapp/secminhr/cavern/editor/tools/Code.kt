package tech.stoneapp.secminhr.cavern.editor.tools

import android.widget.ImageView

class Code(icon: Int): EditorTool(icon) {
    override fun handle(imageView: ImageView, oldContent: String, cursorPos: Int): Pair<String, Int> {
        val length = oldContent.length
        val beforeCursorContent = oldContent.substring(0 until cursorPos)
        val afterCursorContent = oldContent.substring(cursorPos until length)

        val frontNewline = beforeCursorContent.isNotEmpty() && beforeCursorContent.last() != '\n'
        val endNewline = afterCursorContent.isNotEmpty() && afterCursorContent.first() != '\n'
        var symbol = "```\n\n```"
        if(frontNewline) {
            symbol = "\n$symbol"
        }
        if(endNewline) {
            symbol += "\n"
        }
        val offset = symbol.trimEnd('\n').length - 4

        return "$beforeCursorContent$symbol$afterCursorContent" to
                beforeCursorContent.length + offset
    }

    override fun reset(imageView: ImageView) {
        //don't need this
    }
}