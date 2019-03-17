package tech.stoneapp.secminhr.cavern.cavernObject

import androidx.databinding.ObservableInt
import java.io.Serializable
import java.util.*

data class ArticlePreview(val title: String, val author: String, val date: Date,
                          var upvote: ObservableInt, val id: Int, var liked: Boolean): Serializable
