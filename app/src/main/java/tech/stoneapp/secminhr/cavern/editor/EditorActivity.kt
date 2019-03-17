package tech.stoneapp.secminhr.cavern.editor

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.android.volley.AuthFailureError
import com.android.volley.NetworkError
import com.android.volley.NoConnectionError
import kotlinx.android.synthetic.main.activity_edit.*
import tech.stoneapp.secminhr.cavern.R
import tech.stoneapp.secminhr.cavern.api.Cavern
import tech.stoneapp.secminhr.cavern.api.results.LikeResult


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
            Cavern.getInstance(this).publishArticle(title, content).addOnSuccessListener {
                PreferenceManager.getDefaultSharedPreferences(application).edit {
                    putBoolean("article_outdated", true)
                }
                finish()
            }.addOnFailureListener {
                val errorMessage = when(it) {
                    is NetworkError -> "There's something wrong with the server\nPlease try again later"
                    is NoConnectionError -> "Your device seems to be offline\nPlease turn on the internet connection and try again"
                    is LikeResult.NoLoginError -> null
                    is AuthFailureError -> null //hasn't logged in
                    else -> "Some unexpected error happened\nPlease turn off the app and try again later\nWe are sorry for that"
                }
                errorMessage?.let {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                }
            }.execute()
        }
    }

    fun onPreviewSelected(title: String, content: String) {
        val transaction = supportFragmentManager.beginTransaction()
        val bundle = Bundle()
        bundle.putString("title", title)
        bundle.putString("content", content)
        previewFragment.arguments = bundle
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                R.anim.slide_in_left, R.anim.slide_out_right)
        transaction.replace(R.id.mainEditorFrame, previewFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


}
