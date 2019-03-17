package tech.stoneapp.secminhr.cavern.editor

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.textfield.TextInputEditText
import tech.stoneapp.secminhr.cavern.R

class LinkSetupDialog: DialogFragment() {

    var finishListener: ((String, String) -> Unit)? = null
    lateinit var titleInput: TextInputEditText
    lateinit var urlInput: TextInputEditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.link_dialog, null)
        titleInput = view.findViewById(R.id.titleInputText)
        urlInput = view.findViewById(R.id.urlInputText)
        view.findViewById<Button>(R.id.okButton).setOnClickListener {
            var error = false
            if (titleInput.text.isNullOrEmpty()) {
                titleInput.error = "title must not be empty"
                error = true
            }
            if (urlInput.text.isNullOrEmpty()) {
                urlInput.error = "url must not be empty"
                error = true
            }
            if (!error) {
                finishListener?.let {
                    it(titleInput.text.toString(), urlInput.text.toString())
                    dismiss()
                }
            }
        }
        return AlertDialog.Builder(context)
                .setTitle("Setup Link")
                .setView(view)
                .create()
    }

    fun show(manager: FragmentManager?, tag: String?, finishListener: (String, String) -> Unit) {
        super.show(manager, tag)
        this.finishListener = finishListener
    }
}