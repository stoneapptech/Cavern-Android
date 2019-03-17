package tech.stoneapp.secminhr.cavern.articlecontent.markwonUtils

import android.graphics.Color
import android.text.style.ForegroundColorSpan
import ru.noties.markwon.SpannableFactoryDef
import ru.noties.markwon.spans.SpannableTheme

class CavernSpannableFactory: SpannableFactoryDef() {

    override fun heading(theme: SpannableTheme, level: Int): Any? {
        return arrayOf(super.heading(theme, level),
                ForegroundColorSpan(Color.BLACK)
        )
    }
}