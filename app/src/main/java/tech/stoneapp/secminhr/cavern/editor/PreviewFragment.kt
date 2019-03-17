package tech.stoneapp.secminhr.cavern.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import tech.stoneapp.secminhr.cavern.CavernMarkdownTextView
import tech.stoneapp.secminhr.cavern.R

class PreviewFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val title = arguments!!.getString("title")!!
        val content = arguments!!.getString("content")!!
        val view = inflater.inflate(R.layout.fragment_preview, container, false)
        view.findViewById<TextView>(R.id.previewTitle).text = title
        view.findViewById<CavernMarkdownTextView>(R.id.previewContent).setMarkdown(content)
        return view
    }
}
