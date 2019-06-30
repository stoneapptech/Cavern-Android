package stoneapp.secminhr.cavern.api.results

import com.android.volley.*
import com.android.volley.Response.Listener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import stoneapp.secminhr.cavern.api.Cavern
import stoneapp.secminhr.cavern.cavernError.CavernError
import stoneapp.secminhr.cavern.cavernError.NoLoginError
import stoneapp.secminhr.cavern.cavernService.CavernJsonObjectRequest
import stoneapp.secminhr.cavern.get
import stoneapp.secminhr.cavern.getInt

class LikeResult(val id: Int, private val config: FirebaseRemoteConfig, private val queue: RequestQueue): CavernResult<LikeResult> {

    var likeCount = 0
    var isLiked = false

    override fun get(onSuccess: (LikeResult) -> Unit, onFailure: (CavernError) -> Unit) {
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
                Response.ErrorListener {
                    when {
                        it.networkResponse.statusCode == 401 -> onFailure(NoLoginError())
                        it is NetworkError -> onFailure(stoneapp.secminhr.cavern.cavernError.NetworkError())
                        it is NoConnectionError -> onFailure(stoneapp.secminhr.cavern.cavernError.NoConnectionError())
                    }
                }
        )
        queue.add(request)
    }
}