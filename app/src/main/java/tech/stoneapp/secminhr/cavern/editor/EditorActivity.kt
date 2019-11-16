package tech.stoneapp.secminhr.cavern.editor

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import kotlinx.android.synthetic.main.activity_edit.*
import stoneapp.secminhr.cavern.api.Cavern
import stoneapp.secminhr.cavern.cavernError.NetworkError
import stoneapp.secminhr.cavern.cavernError.NoConnectionError
import stoneapp.secminhr.cavern.cavernError.NoLoginError
import tech.stoneapp.secminhr.cavern.R


class EditorActivity : AppCompatActivity() {

    val editorFragment = EditorFragment()
    val previewFragment = PreviewFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        val transaction = supportFragmentManager.beginTransaction()
        val bundle = Bundle()
        bundle.putString("packageName", packageName)
        editorFragment.arguments = bundle
        transaction.add(R.id.mainEditorFrame, editorFragment)
        transaction.commit()

        val manager = PreferenceManager.getDefaultSharedPreferences(this)
        var title = manager.getString("title", "(Untitled)")
        if(title.isNullOrEmpty()) {
            title = "(Untitled)"
        }
        editorFragment.title.set(title)
        editorFragment.content.set(manager.getString("content", ""))

        publishFloatingButton.setOnClickListener {
            val title = editorFragment.title.get()
            val content = editorFragment.content.get()
            if(title.isNullOrEmpty()) {
                Toast.makeText(this, "Title may not be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(content.isNullOrEmpty()) {
                Toast.makeText(this, "Content may not be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            it.isEnabled = false
            Cavern.getInstance(this).publishArticle(title, content).addOnSuccessListener {
                PreferenceManager.getDefaultSharedPreferences(application).edit {
                    putBoolean("article_outdated", true)
                }
                finish()
            }.addOnFailureListener {
                val errorMessage = when(it) {
                    is NetworkError -> "There's something wrong with the server\nPlease try again later"
                    is NoConnectionError -> "Your device seems to be offline\nPlease turn on the internet connection and try again"
                    is NoLoginError -> null
                    else -> "Some unexpected error happened\nPlease turn off the app and try again later\nWe are sorry for that"
                }
                errorMessage?.let {
                    Log.e("Editor", "toast:${it}")
                    Toast.makeText(this, "Editor: ${it}", Toast.LENGTH_SHORT).show()
                }
            }.execute()
        }

        saveFloatingButton.setOnClickListener {
            PreferenceManager.getDefaultSharedPreferences(this).edit {
                putString("title", editorFragment.title.get())
                putString("content", editorFragment.content.get())
            }
            Toast.makeText(this, "draft saved", Toast.LENGTH_SHORT).show()
        }
    }

    fun onPreviewSelected(title: String, content: String) {
        val transaction = supportFragmentManager.beginTransaction()
        val bundle = Bundle()
        bundle.putString("title", title)
        bundle.putString("content", content)
        previewFragment.arguments = bundle
        currentFocus?.let {
            val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                R.anim.slide_in_left, R.anim.slide_out_right)
        transaction.replace(R.id.mainEditorFrame, previewFragment)
        transaction.addToBackStack(null)
        transaction.commit()

//        ObjectAnimator
//                .ofFloat(publishFloatingButton, "y", publishFloatingButton.y, publishFloatingButton.y + 38)
//                .setDuration(350)
//                .start()
    }

    override fun onPause() {
        super.onPause()
        val title = editorFragment.title.get()
        val content = editorFragment.content.get()
        PreferenceManager.getDefaultSharedPreferences(this).edit {
            putString("title", title)
            putString("content", content)
        }
    }
}
