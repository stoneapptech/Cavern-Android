package stoneapp.secminhr.cavern.api.results

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response.Listener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import stoneapp.secminhr.cavern.api.Cavern
import stoneapp.secminhr.cavern.cavernError.CavernError
import stoneapp.secminhr.cavern.cavernObject.Comment
import stoneapp.secminhr.cavern.cavernService.CavernJsonObjectRequest
import stoneapp.secminhr.cavern.getInt
import stoneapp.secminhr.cavern.getJSONArray
import stoneapp.secminhr.cavern.getJSONObject
import stoneapp.secminhr.cavern.getString

class Comments(private val id: Int, private val config: FirebaseRemoteConfig, private val queue: RequestQueue): CavernResult<Comments> {

    val comments = mutableListOf<Comment>()

    override fun get(onSuccess: (Comments) -> Unit, onFailure: (CavernError) -> Unit) {
        val url = "${Cavern.host}/ajax/comment.php?pid=$id"
        val request = CavernJsonObjectRequest(Request.Method.GET, url, null,
                Listener {
                    val arr = it.getJSONArray(config, "comments_key")
                    if(arr.length() > 0) {
                        val nicknames = it.getJSONObject(config, "names_key")
                        val hashes = it.getJSONObject(config, "hash_key")
                        for (i in 0 until arr.length()) {
                            val comment = arr.getJSONObject(i)
                            val id = comment.getInt(config, "id_key")
                            val username = comment.getString(config, "username_key")
                            val content = adjustContent(comment.getString(config, "markdown_key"))
                            val nickname = nicknames.getString(username)
                            val hash = hashes.getString(username)
                            comments += Comment(id, username,
                                    nickname,
                                    content,
                                    "https://www.gravatar.com/avatar/$hash?d=https%3A%2F%2Ftocas-ui.com%2Fassets%2Fimg%2F5e5e3a6.png&s=500")
                        }
                    }
                    onSuccess(this)
                })
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