package tech.stoneapp.secminhr.cavern.articles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import stoneapp.secminhr.cavern.cavernObject.ArticlePreview
import tech.stoneapp.secminhr.cavern.R
import tech.stoneapp.secminhr.cavern.databinding.ArticleListItemBinding

class ArticleListAdapter(val likeListener: (View, ArticlePreview) -> Unit):
        PagedListAdapter<ArticlePreview, ArticleListAdapter.ViewHolder>(diffCallback) {

    class ViewHolder(val binding: ArticleListItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ArticleListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val preview = getItem(position)
        if(preview != null) {
            val binding = holder.binding
            binding.articlePreview = preview
            binding.root.setOnClickListener {
                val b = bundleOf("articleID" to preview.id)
                binding.root.findNavController().navigate(R.id.showContentAction, b)
            }
            binding.thumbButton.setImageResource(
                    if(preview.liked) R.drawable.thumb_up else R.drawable.thumb_up_outline
            )
            binding.thumbButton.setOnClickListener {
                likeListener(binding.root, preview)
            }
            holder.binding.executePendingBindings()
        }

    }

    companion object {
        private val diffCallback = object: DiffUtil.ItemCallback<ArticlePreview>() {
            override fun areItemsTheSame(oldItem: ArticlePreview, newItem: ArticlePreview): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ArticlePreview, newItem: ArticlePreview): Boolean {
                return oldItem == newItem
            }

        }
    }
}