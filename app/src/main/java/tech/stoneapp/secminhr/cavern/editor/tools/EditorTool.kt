package tech.stoneapp.secminhr.cavern.editor.tools

import android.widget.ImageView

abstract class EditorTool(val icon: Int) {

    abstract fun handle(imageView: ImageView, oldContent: String, cursorPos: Int): Pair<String, Int>
    abstract fun reset(imageView: ImageView)
}