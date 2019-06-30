package tech.stoneapp.secminhr.cavern.articlecontent.fontTag

import android.text.TextPaint
import android.text.style.MetricAffectingSpan


class FontTagSpan(val color: Int): MetricAffectingSpan() {
    override fun updateMeasureState(textPaint: TextPaint) {
        textPaint.color = color
    }

    override fun updateDrawState(textPaint: TextPaint?) {
        textPaint?.color = color
    }
}