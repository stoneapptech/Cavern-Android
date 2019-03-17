package tech.stoneapp.secminhr.cavern.api.results

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import tech.stoneapp.secminhr.cavern.api.CavernErrorListener
import tech.stoneapp.secminhr.cavern.cavernService.CavernJsonObjectRequest
import tech.stoneapp.secminhr.cavern.getBoolean
import tech.stoneapp.secminhr.cavern.getJSONObject
import tech.stoneapp.secminhr.cavern.getString

class ArticleContent(val id: Int, private val config: FirebaseRemoteConfig, private val queue: RequestQueue): CavernResult<ArticleContent> {

    class ContentNotExistsError: VolleyError("ContentNotExists")

    var title: String = ""
    var content: String = ""
    var authorNickname: String = ""
    var authorUsername: String = ""
    var isLiked = false

    override fun get(onSuccess: (ArticleContent) -> Unit, onFailure: (VolleyError) -> Unit) {
        val url = "https://stoneapp.tech/cavern/ajax/posts.php?pid=$id"
        val request = CavernJsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { json ->
                    if(json.keys().asSequence().contains(config.getString("message_key") )) {
                        onFailure(ContentNotExistsError())
                    } else {
                        val post = json.getJSONObject(config, "post_key")
                        title = adjustContent(post.getString(config, "title_key"))
                        content = adjustContent(post.getString(config, "content_key"))
                        authorNickname = post.getString(config, "author_key")
                        authorUsername = post.getString(config, "author_username_key")
                        isLiked = post.getBoolean(config, "is_liked")
                        onSuccess(this)
                    }
                },
                CavernErrorListener(onFailure))
        queue.add(request)
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