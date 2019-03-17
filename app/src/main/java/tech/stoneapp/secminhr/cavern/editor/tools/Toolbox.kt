package tech.stoneapp.secminhr.cavern.editor.tools

import android.content.res.Resources
import android.util.Xml
import android.widget.ImageView
import org.xmlpull.v1.XmlPullParser
import tech.stoneapp.secminhr.cavern.R

class Toolbox(resources: Resources, packageName: String) {

    val tools = mutableListOf<EditorTool>()

    init {
        val xmlParser = Xml.newPullParser()
        val inputStream = resources.openRawResource(R.raw.editor_tools)
        xmlParser.setInput(inputStream, null)
        var eventType = xmlParser.eventType
        while(eventType != XmlPullParser.END_DOCUMENT) {
            when(eventType) {
                XmlPullParser.START_TAG -> {
                    if(xmlParser.name == "item") {
                        val icon = xmlParser.getAttributeValue(null, "icon")
                        val id = resources.getIdentifier(icon, "drawable", packageName)
                        val className = xmlParser.getAttributeValue(null, "action")
                        val clazz= Class.forName(className)
                                .getConstructor(Int::class.java).newInstance(id) as EditorTool
                        tools += clazz
                    }
                }
            }
            eventType = xmlParser.next()
        }
    }

    fun resetAll(views: List<ImageView>) {
        tools.forEachIndexed { index, tool ->
            tool.reset(views[index])
        }
    }
}