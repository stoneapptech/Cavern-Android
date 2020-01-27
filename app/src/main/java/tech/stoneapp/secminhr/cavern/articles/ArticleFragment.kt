package tech.stoneapp.secminhr.cavern.articles

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.article_fragment.*
import stoneapp.secminhr.cavern.cavernObject.Account
import stoneapp.secminhr.cavern.cavernObject.ArticlePreview
import tech.stoneapp.secminhr.cavern.activity.MainActivity
import tech.stoneapp.secminhr.cavern.databinding.ArticleFragmentBinding
import tech.stoneapp.secminhr.cavern.editor.NewArticleActivity


class ArticleFragment: Fragment() {
    lateinit var viewModel: ArticleViewModel
    lateinit var adapter: ArticleListAdapter
    var contentReady = ObservableBoolean(false)
    val userCanPost = ObservableBoolean(false)
    val hasUserLoggedIn = ObservableBoolean(false)


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(ArticleViewModel::class.java)
        refreshLayout.setColorSchemeResources(tech.stoneapp.secminhr.cavern.R.color.colorPrimary, tech.stoneapp.secminhr.cavern.R.color.colorPrimaryDark, tech.stoneapp.secminhr.cavern.R.color.colorAccent)
        refreshLayout.setOnRefreshListener {
            viewModel.getArticle().observe(this, Observer<PagedList<ArticlePreview>> { arr ->
                //arr will once cleanup to null before request
                //thus don't need to do anything if receive null
                arr?.let {
                    viewModel.firstVisible = 0
                    viewModel.top = 0
                    adapter.submitList(it)
                    contentReady.set(true)
                    refreshLayout.isRefreshing = false
                }
            })
        }
        (activity as MainActivity)
                .hasUserLoggedIn.addOnPropertyChangedCallback(object: Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                this@ArticleFragment.hasUserLoggedIn.set((sender as ObservableBoolean).get())
            }
        })
        (activity as MainActivity).loggedUserModel.user
                .addOnPropertyChangedCallback(object: Observable.OnPropertyChangedCallback() {
                    override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                        val canPost = (sender as ObservableField<Account?>).get()?.role?.canPostArticle ?: false
                        this@ArticleFragment.userCanPost.set(canPost)
                    }
                })
        hasUserLoggedIn.set((activity as MainActivity).hasUserLoggedIn.get())
        val canPost = (activity as MainActivity).loggedUserModel.user.get()?.role?.canPostArticle ?: false
        this.userCanPost.set(canPost)

        addArticleButton.setOnClickListener {
            val intent = Intent(activity, NewArticleActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = ArticleFragmentBinding.inflate(inflater, container, false)
        adapter = ArticleListAdapter { item, preview ->
            viewModel.like(preview.id, onSuccess = { liked, likeCount ->
                preview.liked = liked
                preview.upvote.set(likeCount)
                item.findViewById<TextView>(tech.stoneapp.secminhr.cavern.R.id.numTextView).text = likeCount.toString()
                val button = item.findViewById<ImageButton>(tech.stoneapp.secminhr.cavern.R.id.thumbButton)
                if(liked) {
                    button.setImageResource(tech.stoneapp.secminhr.cavern.R.drawable.thumb_up)
                } else {
                    button.setImageResource(tech.stoneapp.secminhr.cavern.R.drawable.thumb_up_outline)
                }
            }, errorHandler = {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            })
        }
        binding.adapter = adapter
        binding.fragment = this
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        val index = (articleListview.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        val view = articleListview.getChildAt(0)
        val top = if(view == null) 0 else view.top - articleListview.paddingTop
        viewModel.firstVisible = index
        viewModel.top = top
    }

    override fun onResume() {
        super.onResume()
        val manager = LinearLayoutManager(context)
        manager.orientation = RecyclerView.VERTICAL
        articleListview.layoutManager = manager
        val divider = DividerItemDecoration(context, RecyclerView.VERTICAL)
        articleListview.addItemDecoration(divider)
        viewModel.getArticle().observe(this, Observer {
            adapter.submitList(it)
            contentReady.set(true)

            val getFromModel = (articleListview.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() == 0
            val index = if (getFromModel) viewModel.firstVisible else
                (articleListview.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            val view = articleListview.getChildAt(0)
            val top = when {
                view == null -> 0
                getFromModel -> viewModel.top
                else -> view.top - articleListview.paddingTop
            }

            (articleListview.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(index, top)
        })
    }
}