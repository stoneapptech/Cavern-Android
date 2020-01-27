package tech.stoneapp.secminhr.cavern.editor

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.Observable
import androidx.fragment.app.commit
import kotlinx.android.synthetic.main.activity_edit.*
import stoneapp.secminhr.cavern.cavernError.CavernError
import stoneapp.secminhr.cavern.cavernError.NetworkError
import stoneapp.secminhr.cavern.cavernError.NoConnectionError
import stoneapp.secminhr.cavern.cavernError.NoLoginError
import tech.stoneapp.secminhr.cavern.R


abstract class EditorActivity : AppCompatActivity() {

    val editorFragment = EditorFragment()
    val previewFragment = PreviewFragment()
    abstract fun send(title: String?, content: String?, onSuccess: () -> Unit, onFailure: (CavernError) -> Unit)
    abstract fun titleCheck(title: String?): Boolean
    abstract fun contentCheck(content: String?): Boolean
    abstract fun save(title: String?, content: String?)
    abstract fun restore(): Pair<String, String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        val transaction = supportFragmentManager.beginTransaction()
        val bundle = Bundle()
        bundle.putString("packageName", packageName)
        editorFragment.arguments = bundle
        transaction.add(R.id.mainEditorFrame, editorFragment)
        transaction.commit()
        val (title, content) = restore()
        editorFragment.title.set(title)
        editorFragment.content.set(content)
        editorFragment.title.addOnPropertyChangedCallback(object: Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                publishFloatingButton.isEnabled = checkTitleAndContent()
            }
        })
        editorFragment.content.addOnPropertyChangedCallback(object: Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                publishFloatingButton.isEnabled = checkTitleAndContent()
            }
        })
        publishFloatingButton.isEnabled = false
        publishFloatingButton.setOnClickListener {
            it.isEnabled = false
            val title = editorFragment.title.get()
            val content = editorFragment.content.get()
            send(title, content, onSuccess = {
                finish()
            }, onFailure = { error ->
                val errorMessage = when(error) {
                    is NetworkError -> "There's something wrong with the server\nPlease try again later"
                    is NoConnectionError -> "Your device seems to be offline\nPlease turn on the internet connection and try again"
                    is NoLoginError -> null
                    else -> "Some unexpected error happened\nPlease turn off the app and try again later\nWe are sorry for that"
                }
                errorMessage?.let {
                    Log.e("Editor", "toast:${it}")
                    Toast.makeText(this, "Editor: ${it}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        saveFloatingButton.setOnClickListener {
            save(editorFragment.title.get(), editorFragment.content.get())
            Toast.makeText(this, "draft saved", Toast.LENGTH_SHORT).show()
        }
    }

    fun onPreviewSelected(title: String, content: String) {
        currentFocus?.let {
            val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
        val bundle = Bundle()
        bundle.putString("title", title)
        bundle.putString("content", content)
        previewFragment.arguments = bundle

        supportFragmentManager.commit {
            setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                    R.anim.slide_in_left, R.anim.slide_out_right)
            replace(R.id.mainEditorFrame, previewFragment)
            addToBackStack(null)
        }
    }

    private fun checkTitleAndContent(): Boolean {
        val title = editorFragment.title.get()
        val content = editorFragment.content.get()
        return titleCheck(title) && contentCheck(content)
    }

    override fun onPause() {
        super.onPause()
        val title = editorFragment.title.get()
        val content = editorFragment.content.get()
        save(title, content)
    }
}
