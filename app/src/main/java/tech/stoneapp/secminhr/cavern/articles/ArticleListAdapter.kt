package tech.stoneapp.secminhr.cavern.articles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import stoneapp.secminhr.cavern.cavernObject.ArticlePreview
import tech.stoneapp.secminhr.cavern.R
import tech.stoneapp.secminhr.cavern.databinding.ArticleListItemBinding

class ArticleListAdapter(val array: ArrayList<ArticlePreview>, val likeListener: (View, ArticlePreview) -> Unit):
        RecyclerView.Adapter<ArticleListAdapter.ViewHolder>() {

    class ViewHolder(val binding: ArticleListItemBinding): RecyclerView.ViewHolder(binding.root)

    fun addAll(collection: MutableCollection<out ArticlePreview>) {
        array.addAll(collection.sortedByDescending { it.id })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ArticleListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding
        binding.articlePreview = array[position]
        val b = bundleOf("articleID" to array[position].id)
        binding.root.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_navigation_article_to_articleContentFragment, b)
        )
        binding.thumbButton.setImageResource(
                if(array[position].liked) R.drawable.thumb_up else R.drawable.thumb_up_outline
        )
        binding.thumbButton.setOnClickListener {
            likeListener(binding.root, array[position])
        }
        holder.binding.executePendingBindings()
    }

    override fun getItemCount() = array.size
}