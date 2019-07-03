package stoneapp.secminhr.cavern.api.results

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import stoneapp.secminhr.cavern.api.Cavern
import stoneapp.secminhr.cavern.cavernError.CavernError
import stoneapp.secminhr.cavern.cavernError.NotExistsError
import stoneapp.secminhr.cavern.cavernService.CavernJsonObjectRequest
import stoneapp.secminhr.cavern.getBoolean
import stoneapp.secminhr.cavern.getJSONObject
import stoneapp.secminhr.cavern.getString

class ArticleContent(val id: Int, private val config: FirebaseRemoteConfig, private val queue: RequestQueue): CavernResult<ArticleContent> {

    var title: String = ""
    var content: String = ""
    var authorNickname: String = ""
    var authorUsername: String = ""
    var isLiked = false

    override fun get(onSuccess: (ArticleContent) -> Unit, onFailure: (CavernError) -> Unit) {
        val url = "${Cavern.host}/ajax/posts.php?pid=${id}"
        val request = CavernJsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { json ->
                    val post = json.getJSONObject(config, "post_key")
                    title = adjustContent(post.getString(config, "title_key"))
                    content = adjustContent(post.getString(config, "content_key"))
                    authorNickname = post.getString(config, "author_key")
                    authorUsername = post.getString(config, "author_username_key")
                    isLiked = post.getBoolean(config, "is_liked")
                    onSuccess(this)
                },
                Response.ErrorListener {
                    if(it.networkResponse.statusCode == 404) {
                        onFailure(NotExistsError())
                    }
                }
        )
        queue.add(request)
    }



    private fun adjustContent(content: String): String {
        var adjusted = content.replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("\\/", "/")
                .replace("&quot;", "\"")
                .replace("&amp;", "&")
                .replace("&amp", "&")
        val regex = Regex("@[a-zA-z0-9]+")
        adjusted = regex.replace(adjusted) {
            it.value + "@"
        }
        return adjusted
    }

}