package tech.stoneapp.secminhr.cavern.api.results

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import tech.stoneapp.secminhr.cavern.api.Cavern
import tech.stoneapp.secminhr.cavern.cavernObject.Account
import tech.stoneapp.secminhr.cavern.cavernService.CavernJsonObjectRequest
import tech.stoneapp.secminhr.cavern.getInt
import tech.stoneapp.secminhr.cavern.getString

class SessionUser(private val config: FirebaseRemoteConfig, private val queue: RequestQueue):
        User("", "", config, queue) {

    class SessionExpiredException: VolleyError("Session has expired")

    override fun get(onSuccess: (User) -> Unit, onFailure: (VolleyError) -> Unit) {
        val userURL = "${Cavern.host}/ajax/user.php"
        val subRequest = CavernJsonObjectRequest(Request.Method.GET, userURL, null,
                Response.Listener {
                    val username = it.getString(config, "username_key")
                    val nickname = it.getString(config, "author_key")
                    val email = it.getString(config, "email_key")
                    val imageLink = "https://www.gravatar.com/avatar/${it.getString(config, "hash_key")}?d=https%3A%2F%2Ftocas-ui.com%2Fassets%2Fimg%2F5e5e3a6.png&s=500"
                    val roleLevel = it.getInt(config, "role_key")
                    val postCount = it.getInt(config, "posts_count_key")
                    RoleDetail(roleLevel).get({ roleDetail ->
                        account = Account(username, nickname, roleDetail.role, imageLink, postCount, email)
                        onSuccess(this)
                    }) {
                        onFailure(it)
                    }
                },
                Response.ErrorListener {
                    onFailure(SessionExpiredException())
                })
        queue.add(subRequest)
    }

}