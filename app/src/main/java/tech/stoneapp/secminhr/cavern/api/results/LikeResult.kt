package tech.stoneapp.secminhr.cavern.api.results

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response.Listener
import com.android.volley.VolleyError
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import tech.stoneapp.secminhr.cavern.api.Cavern
import tech.stoneapp.secminhr.cavern.api.CavernErrorListener
import tech.stoneapp.secminhr.cavern.cavernService.CavernJsonObjectRequest
import tech.stoneapp.secminhr.cavern.get
import tech.stoneapp.secminhr.cavern.getInt

class LikeResult(val id: Int, private val config: FirebaseRemoteConfig, private val queue: RequestQueue): CavernResult<LikeResult> {

    class NoLoginError: VolleyError("You hasn't logged in")

    var likeCount = 0
    var isLiked = false

    override fun get(onSuccess: (LikeResult) -> Unit, onFailure: (VolleyError) -> Unit) {
        val url = "${Cavern.host}/ajax/like.php?pid=$id"
        val request = CavernJsonObjectRequest(Request.Method.GET, url, null,
                Listener {
                    val status = it.get(config, "status_key")
                    when(status) {
                        is Boolean -> {
                            likeCount = it.getInt(config, "likes_key")
                            isLiked = status
                            onSuccess(this)
                        }
                    }
                },
                CavernErrorListener(onFailure))
        queue.add(request)
    }
}