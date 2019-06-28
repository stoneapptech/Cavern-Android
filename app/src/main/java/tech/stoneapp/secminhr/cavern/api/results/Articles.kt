package tech.stoneapp.secminhr.cavern.api.results

import androidx.databinding.ObservableInt
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import org.json.JSONArray
import tech.stoneapp.secminhr.cavern.api.Cavern
import tech.stoneapp.secminhr.cavern.api.CavernErrorListener
import tech.stoneapp.secminhr.cavern.cavernObject.ArticlePreview
import tech.stoneapp.secminhr.cavern.cavernService.CavernJsonObjectRequest
import tech.stoneapp.secminhr.cavern.getBoolean
import tech.stoneapp.secminhr.cavern.getInt
import tech.stoneapp.secminhr.cavern.getJSONArray
import tech.stoneapp.secminhr.cavern.getString
import java.text.SimpleDateFormat
import java.util.*

class Articles(private val config: FirebaseRemoteConfig, private val queue: RequestQueue): CavernResult<Articles> {

    val articles: MutableList<ArticlePreview> = mutableListOf()

    override fun get(onSuccess: (Articles) -> Unit, onFailure: (VolleyError) -> Unit) {
        val url = "${Cavern.host}/ajax/posts.php?page="
        val request = CavernJsonObjectRequest(Request.Method.GET, "${url}1", null,
                Response.Listener { json ->
                    val postsNumber = json.getInt(config, "total_posts_num")
                    val pageLimit = json.getInt(config, "page_limit")
                    val totalPages = if(postsNumber % pageLimit == 0) {
                        postsNumber/pageLimit
                    } else {
                        postsNumber/pageLimit + 1
                    }
                    articles += parseJson(json.getJSONArray(config, "posts_key"))
                    if(totalPages > 1) {
                        for(i in 2..totalPages) {
                            val subRequest = CavernJsonObjectRequest(Request.Method.GET, "$url$i", null,
                                    Response.Listener {
                                        articles += parseJson(it.getJSONArray(config, "posts_key"))
                                        if(i == totalPages) {
                                            onSuccess(this)
                                        }
                                    },
                                    CavernErrorListener(onFailure))
                            queue.add(subRequest)
                        }
                    }
                },
                CavernErrorListener(onFailure))
        queue.add(request)
    }

    private fun parseJson(jsonArray: JSONArray): Array<ArticlePreview> {
        var articleArr = arrayOf<ArticlePreview>()
        for(i in 0 until jsonArray.length()) {
            val json = jsonArray.getJSONObject(i)
            val title = adjustContent(json.getString(config, "title_key"))
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            val dateString =  json.getString(config, "date_key")
            val date = dateFormat.parse(dateString)
            val likes = json.getString(config, "upvote_key").toInt()
            val id = json.getString(config, "pid_key").toInt()
            val author = json.getString(config, "author_key")
            val liked = json.getBoolean(config, "is_liked")
            articleArr += ArticlePreview(title, author, date, ObservableInt(likes), id, liked)
        }
        return articleArr
    }

    private fun adjustContent(content: String): String {
        var adjusted = content.replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("\\/", "/")
                .replace("&quot;", "\"")
        val regex = Regex("@[a-zA-z0-9]+")
        adjusted = regex.replace(adjusted) {
            it.value + "@"
        }
        return adjusted
    }
}