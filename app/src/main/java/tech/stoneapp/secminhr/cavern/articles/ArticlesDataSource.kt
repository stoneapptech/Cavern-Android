package tech.stoneapp.secminhr.cavern.articles

import android.content.Context
import androidx.paging.PageKeyedDataSource
import stoneapp.secminhr.cavern.api.Cavern
import stoneapp.secminhr.cavern.cavernObject.ArticlePreview

class ArticlesDataSource(val context: Context): PageKeyedDataSource<Int, ArticlePreview>() {
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ArticlePreview>) {
        Cavern.getInstance(context).getArticles(1, params.requestedLoadSize).addOnSuccessListener {
            callback.onResult(it.articles, 0, it.totalPageCount, null, 2)
        }.execute()
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ArticlePreview>) {
        Cavern.getInstance(context).getArticles(params.key, params.requestedLoadSize).addOnSuccessListener {
            callback.onResult(it.articles, params.key + 1)
        }.execute()
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ArticlePreview>) {
        //intentionally left empty
    }
}