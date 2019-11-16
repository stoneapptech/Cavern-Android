package tech.stoneapp.secminhr.cavern.articles

import android.content.Context
import androidx.paging.DataSource
import stoneapp.secminhr.cavern.cavernObject.ArticlePreview

class ArticlesDataSourceFactory(val context: Context): DataSource.Factory<Int, ArticlePreview>() {

    override fun create(): DataSource<Int, ArticlePreview> {
        return ArticlesDataSource(context)
    }
}