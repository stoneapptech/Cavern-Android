package tech.stoneapp.secminhr.cavern.editor

import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_editor.*
import tech.stoneapp.secminhr.cavern.R
import tech.stoneapp.secminhr.cavern.databinding.FragmentEditorBinding
import tech.stoneapp.secminhr.cavern.editor.tools.*

class EditorFragment: Fragment(), ContentHolder {

    enum class ListStyle {
        Bulleted, Numbered
    }

    override val title: ObservableField<String> = ObservableField("(Untitled)")
    override val content: ObservableField<String> = ObservableField("")
    lateinit var toolbox: Toolbox

    //whether it is in list mode
    var inListMode = false
    //the current style of the list
    var listStyle: ListStyle? = null

    //the current number of list
    //will only be use for number list
    var listNum = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentEditorBinding.inflate(inflater)
        binding.contentHolder = this
        toolbox = Toolbox(resources, arguments!!.getString("packageName")!!)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity!! as AppCompatActivity).setSupportActionBar(titleToolbar)

        toolbox.tools.forEach {
            val view = ImageView(context)
            view.setImageResource(it.icon)
            val params = ViewGroup.LayoutParams(
                    46 * resources.displayMetrics.densityDpi / 160,
                    46 * resources.displayMetrics.densityDpi / 160
            )
            view.scaleType = ImageView.ScaleType.FIT_CENTER
            view.setOnClickListener { _ ->
                toolbox.resetAll(toolbar3.children.toList() as List<ImageView>)
                when (it) {
                    is Link -> {
                        val dialog = LinkSetupDialog()
                        dialog.show(fragmentManager, "Setup Link Dialog") { title, url ->
                            it.title = title
                            it.url = url
                            val result = it.handle(view, content.get()!!, contentEditText.selectionStart)

                            contentEditText.setText(result.first)
                            contentEditText.requestFocusFromTouch()
                            Selection.setSelection(contentEditText.text, result.second)
                        }
                    }
                    is Photo -> {
                        val dialog = ImageSetupDialog()
                        dialog.show(fragmentManager, "Setup Link Dialog") { altText, url ->
                            it.altText = altText
                            it.url = url
                            val result = it.handle(view, content.get()!!, contentEditText.selectionStart)

                            contentEditText.setText(result.first)
                            contentEditText.requestFocusFromTouch()
                            Selection.setSelection(contentEditText.text, result.second)
                        }
                    }
                    is BulletedList -> {
                        if (inListMode && listStyle == ListStyle.Bulleted) {
                            inListMode = false
                            listStyle = null
                            listNum = 0
                            return@setOnClickListener
                        }
                        listStyle = ListStyle.Bulleted
                        inListMode = true
                    }
                    is NumberedList -> {
                        if (inListMode && listStyle == ListStyle.Numbered) {
                            inListMode = false
                            listStyle = null
                            listNum = 0
                            return@setOnClickListener
                        }
                        listNum = 1
                        listStyle = ListStyle.Numbered
                        inListMode = true
                    }
                }
                val result = it.handle(view, content.get()!!, contentEditText.selectionStart)

                contentEditText.setText(result.first)
                contentEditText.requestFocusFromTouch()
                Selection.setSelection(contentEditText.text, result.second)
            }
            toolbar3.addView(view, toolbar3.childCount, params)
        }

        contentEditText.addTextChangedListener(object: TextWatcher {

            var preIsNewLine = false
            var isNewline = false
            //Following two are use for selection positioning
            var beforeLength = 0
            var offset = 0

            override fun afterTextChanged(content: Editable?) {
                if(isNewline && inListMode) {
                    var symbol = if(listStyle == ListStyle.Bulleted) "- " else "${++listNum}. "
                    val cursorPos = contentEditText.selectionStart
                    val beforeCursorContent = content?.substring(0 until cursorPos)
                    val afterCursorContent = content?.substring(cursorPos until content.length)

                    val frontNewline = !beforeCursorContent.isNullOrEmpty() && beforeCursorContent.last() != '\n'
                    val endNewline = !afterCursorContent.isNullOrEmpty() && afterCursorContent.first() != '\n'
                    if(frontNewline) {
                        symbol = "\n$symbol"
                    }
                    if(endNewline) {
                        symbol += "\n"
                    }
                    val offset = symbol.trimEnd('\n').length
                    beforeLength = beforeCursorContent?.length ?: 0
                    this.offset = offset
                    contentEditText.setText("$beforeCursorContent$symbol$afterCursorContent")
                } else if(!isNewline && preIsNewLine && inListMode) { //click enter
                    contentEditText.requestFocusFromTouch()
                    Selection.setSelection(contentEditText.text,
                            beforeLength + offset)
                    beforeLength = 0
                    offset = 0
                }

            }

            override fun beforeTextChanged(content: CharSequence?, start: Int, count: Int, after: Int) {
                //doesn't need this function
            }

            override fun onTextChanged(content: CharSequence?, start: Int, before: Int, count: Int) {
                preIsNewLine = isNewline
                isNewline = if(content.isNullOrEmpty()) {
                    false
                } else {
                    val newText = content.subSequence(start, start + count)
                    if(newText.isEmpty()) {
                        false
                    } else {
                        newText.last() == '\n'
                    }
                }
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.editor_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val hash = hashMapOf(
                R.id.preview_menu_item to ::onPreviewSelected
        )
        hash[item?.itemId]?.invoke()
        return hash.containsKey(item?.itemId)
    }

    private fun onPreviewSelected() {
        (activity as EditorActivity).onPreviewSelected(title.get()!!, content.get()!!)
    }
}