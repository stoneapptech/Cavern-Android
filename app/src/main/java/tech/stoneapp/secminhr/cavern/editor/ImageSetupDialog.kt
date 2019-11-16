package tech.stoneapp.secminhr.cavern.editor

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.textfield.TextInputEditText
import tech.stoneapp.secminhr.cavern.R

class ImageSetupDialog: DialogFragment() {

    lateinit var altText: TextInputEditText
    lateinit var imageUrl: TextInputEditText
    var finishListener: ((String, String) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.image_dialog, null)
        altText = view.findViewById(R.id.altInputText)
        imageUrl = view.findViewById(R.id.imageUrlInputText)
        view.findViewById<Button>(R.id.imageOkButton).setOnClickListener {
            var error = false
            if (altText.text.isNullOrEmpty()) {
                altText.error = "title must not be empty"
                error = true
            }
            if (imageUrl.text.isNullOrEmpty()) {
                imageUrl.error = "url must not be empty"
                error = true
            }
            if (!error) {
                finishListener?.let {
                    it(altText.text.toString(), imageUrl.text.toString())
                    dismiss()
                }
            }
        }
        return AlertDialog.Builder(context)
                .setTitle("Setup Link")
                .setView(view)
                .create()
    }

    fun show(manager: FragmentManager, tag: String?, finishListener: (String, String) -> Unit) {
        super.show(manager, tag)
        this.finishListener = finishListener
    }
}