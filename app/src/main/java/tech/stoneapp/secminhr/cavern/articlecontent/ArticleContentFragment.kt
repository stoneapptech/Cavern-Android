package tech.stoneapp.secminhr.cavern.articlecontent

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_article_content.*
import stoneapp.secminhr.cavern.cavernError.NetworkError
import stoneapp.secminhr.cavern.cavernError.NoConnectionError
import stoneapp.secminhr.cavern.cavernError.NotExistsError
import tech.stoneapp.secminhr.cavern.R
import tech.stoneapp.secminhr.cavern.accountInfo.bottomAuthorDialog.BottomAuthorDialog
import tech.stoneapp.secminhr.cavern.databinding.FragmentArticleContentBinding

class ArticleContentFragment : Fragment() {

    private var authorUsername: String = ""
    lateinit var viewModel: ArticleContentViewModel
    lateinit var adapter: CommentListAdapter
    var pid = -1
    var commentID = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(activity!!).get(ArticleContentViewModel::class.java)
        val binding = FragmentArticleContentBinding.inflate(inflater)
        pid = arguments?.getInt("articleID")!!
        var contentReady = false
        var commentReady = false
        arguments?.getString("argument")?.let {
            Log.e("ArticleContent", it)
            if(it.contains("#")) {
                val list = it.split("#")
                pid = list[0].toInt()
                commentID = (list[1].split("-"))[1].toInt()
            } else {
                pid = it.toInt()
            }
        }
        viewModel.getArticleContent(pid) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }.observe(this, Observer { article ->
            binding.article = article
            if(article.isLiked) {
                likeButton.setImageResource(R.drawable.thumb_up)
            } else {
                likeButton.setImageResource(R.drawable.thumb_up_outline)
            }
            context?.let {
                content_text.addOnUsernameClickedListener {
                    showUserDialog(it.username)
                }.addErrorListener {
                    val errorMessage = when (it) {
                        is NetworkError -> "There's something wrong with the server\nPlease try again later"
                        is NoConnectionError -> "Your device seems to be offline\nPlease turn on the internet connection and try again"
                        is NotExistsError -> "Author doesn't exist"
                        else -> "Some unexpected error happened\nPlease turn off the app and try again later\nWe are sorry for that"
                    }
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
                content_text.setMarkdown(article.content)
                authorUsername = article.authorUsername
                contentReady = true
                if(commentReady && contentReady) {
                    progressBar.visibility = View.GONE
                    content_layout.visibility = View.VISIBLE
                }
            }
        })
        adapter = CommentListAdapter(context!!,
                arrayListOf(),
                activity!!,
                this::showUserDialog)
        viewModel.getComments(pid) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }.observe(this, Observer {
            adapter.clear()
            adapter.addAll(it.toMutableList())
            adapter.notifyDataSetChanged()
            commentReady = true
            if(commentReady && contentReady) {
                progressBar.visibility = View.GONE
                content_layout.visibility = View.VISIBLE
            }
        })
        binding.executePendingBindings()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        authorButton.setOnClickListener {
            showUserDialog(authorUsername)
        }
        commentListView.adapter = adapter
        commentListView.setOnItemClickListener { _, _, i, _ ->
            //use i-1 because header also counts
            if(i > 0) {
                showUserDialog(adapter.array[i-1].commenterUsername)
            }
        }
        if(commentListView.headerViewsCount < 1) {
            commentListView.addHeaderView(layoutInflater.inflate(R.layout.comment_list_header, null))
        }
        likeButton.setOnClickListener {
            viewModel.like(pid, errorHandler = {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }) { isLiked ->
                PreferenceManager.getDefaultSharedPreferences(context).edit {
                    putBoolean("article_outdated", true)
                }
                if(isLiked) {
                    likeButton.setImageResource(R.drawable.thumb_up)
                } else {
                    likeButton.setImageResource(R.drawable.thumb_up_outline)
                }
            }
        }
    }

    private fun showUserDialog(username: String) {
        val bottomFragment = BottomAuthorDialog.newInstance(username)
        bottomFragment.show(fragmentManager, "Author")
    }
}
