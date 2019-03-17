package tech.stoneapp.secminhr.cavern.articlecontent.fontTag

import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import ru.noties.markwon.SpannableBuilder
import ru.noties.markwon.SpannableConfiguration
import ru.noties.markwon.html.api.HtmlTag
import ru.noties.markwon.renderer.html2.tag.TagHandler

class FontTagHandler(val resource: Resources, val packageName: String): TagHandler() {
    override fun handle(configuration: SpannableConfiguration, builder: SpannableBuilder, tag: HtmlTag) {
        if(tag.isBlock) {
            visitChildren(configuration, builder, tag.asBlock)
        }
        val colorAttr = tag.attributes()["color"]
        colorAttr?.let { colorStr ->
            val id = resource.getIdentifier(colorStr.toLowerCase(), "color", packageName)
            val color: Int
            if(id != 0) { //color name form
                color = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    resource.getColor(id, null)
                } else {
                    resource.getColor(id)
              }
            } else {
                color = if(colorAttr.startsWith("#")) { //#RRGGCC form
                    try {
                        Color.parseColor(colorAttr)
                    } catch (e: IllegalArgumentException) {
                        Color.BLACK
                    }
                } else if(colorAttr.count() == 6) { //RRGGCC form
                    try {
                        Color.parseColor("#$colorAttr")
                    } catch (e: IllegalArgumentException) {
                        Color.BLACK
                    }
                } else if (colorAttr.startsWith("r")) { //rgb() form
                    val rgbStr = colorAttr.removeSurrounding("rgb(", ")")
                    val rgb = rgbStr.split(",").map { it.trim() }.map { it.toInt() }
                    if(rgb.size == 3) Color.argb(255, rgb[0], rgb[1], rgb[2]) else Color.BLACK
                } else {
                    Color.BLACK
                }
            }
            SpannableBuilder.setSpans(builder, FontTagSpan(color),tag.start(), tag.end())
        }
    }
}