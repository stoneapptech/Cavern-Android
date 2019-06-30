package stoneapp.secminhr.cavern.api.results

import com.android.volley.*
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import org.json.JSONException
import stoneapp.secminhr.cavern.api.Cavern
import stoneapp.secminhr.cavern.cavernError.CavernError
import stoneapp.secminhr.cavern.cavernError.NotExistsError
import stoneapp.secminhr.cavern.cavernObject.Account
import stoneapp.secminhr.cavern.cavernObject.Role
import stoneapp.secminhr.cavern.cavernService.CavernJsonObjectRequest
import stoneapp.secminhr.cavern.getInt
import stoneapp.secminhr.cavern.getString

class Author(val username: String, private val config: FirebaseRemoteConfig, private val queue: RequestQueue): CavernResult<Author> {

    var account = Account(username, "", Role(-1, "", false, false, false), "", 0)

    override fun get(onSuccess: (Author) -> Unit, onFailure: (CavernError) -> Unit) {

        val url = "${Cavern.host}/ajax/user.php?username=$username"
        val request = CavernJsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener {
                    val email = try {
                        it.getString(config.getString("email_key"))
                    } catch (e: JSONException) { //value not exists
                        null
                    }
                    RoleDetail(it.getInt(config, "role_key")).get({ detail ->
                        account = Account(username,
                                it.getString(config, "author_key"),
                                detail.role,
                                "https://www.gravatar.com/avatar/${it.getString(config, "hash_key")}?d=https%3A%2F%2Ftocas-ui.com%2Fassets%2Fimg%2F5e5e3a6.png&s=500",
                                it.getInt(config, "posts_count_key"),
                                email
                        )
                        onSuccess(this)
                    }) {
                        onFailure(it)
                    }
                },
                Response.ErrorListener {
                    when {
                        it is NetworkError -> onFailure(stoneapp.secminhr.cavern.cavernError.NetworkError())
                        it is NoConnectionError -> onFailure(stoneapp.secminhr.cavern.cavernError.NoConnectionError())
                        it.networkResponse.statusCode == 404 -> onFailure(NotExistsError())
                    }
                }
        )
        queue.add(request)
    }
}