package tech.stoneapp.secminhr.cavern.articlecontent

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.squareup.picasso.Picasso
import stoneapp.secminhr.cavern.cavernError.NetworkError
import stoneapp.secminhr.cavern.cavernError.NoConnectionError
import stoneapp.secminhr.cavern.cavernError.NotExistsError
import stoneapp.secminhr.cavern.cavernObject.Comment
import tech.stoneapp.secminhr.cavern.CavernMarkdownTextView
import tech.stoneapp.secminhr.cavern.R
import tech.stoneapp.secminhr.cavern.databinding.CommentsItemBinding

class CommentListAdapter(context: Context,
                         val array: ArrayList<Comment>,
                         val activity: Activity,
                         val showUserDialog: (String) -> Unit):
        ArrayAdapter<Comment>(context, 0, array) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = try {
            DataBindingUtil.getBinding<CommentsItemBinding>(convertView!!)
        } catch (e: NullPointerException) {
            CommentsItemBinding.inflate(LayoutInflater.from(context), parent, false)
        } ?: CommentsItemBinding.inflate(LayoutInflater.from(context), parent, false)
        binding.comment = array[position]
        Picasso.get().load(array[position].imageUrl).into(binding.root.findViewById<ImageView>(R.id.imageView))
        val markdownView = binding.root.findViewById<CavernMarkdownTextView>(R.id.commentContentTextView)
        markdownView.addOnUsernameClickedListener {
            showUserDialog(it.username)
        }.addErrorListener {
            var errorMessage = when (it) {
                is NetworkError -> "There's something wrong with the server\nPlease try again later"
                is NoConnectionError -> "Your device seems to be offline\nPlease turn on the internet connection and try again"
                is NotExistsError -> "Author doesn't exist"
                else -> "Some unexpected error happened\nPlease turn off the app and try again later\nWe are sorry for that"
            }
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
        markdownView.setMarkdown(array[position].content)
        return binding.root
    }

}