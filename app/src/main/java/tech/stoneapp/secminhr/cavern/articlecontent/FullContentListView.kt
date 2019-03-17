package tech.stoneapp.secminhr.cavern.articlecontent

import android.content.Context
import android.util.AttributeSet
import android.widget.ListView

class FullContentListView: ListView {
    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var height = 0
        for(i in 0 until adapter.count) {
            height += adapter.getView(i, null, this).height
            height += dividerHeight
        }
        height -= dividerHeight
        val customHeight = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 2, MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, customHeight)
        val params = layoutParams
        params.height = measuredHeight
    }
}