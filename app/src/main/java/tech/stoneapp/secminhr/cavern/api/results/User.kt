package tech.stoneapp.secminhr.cavern.api.results

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import org.jsoup.Jsoup
import tech.stoneapp.secminhr.cavern.api.Cavern
import tech.stoneapp.secminhr.cavern.api.CavernErrorListener
import tech.stoneapp.secminhr.cavern.cavernObject.Account
import tech.stoneapp.secminhr.cavern.cavernObject.Role
import tech.stoneapp.secminhr.cavern.cavernService.CavernJsonObjectRequest
import tech.stoneapp.secminhr.cavern.cavernService.CavernStringRequest
import tech.stoneapp.secminhr.cavern.getInt
import tech.stoneapp.secminhr.cavern.getString

open class User(val username: String,
           private val password: String,
           private val remoteConfig: FirebaseRemoteConfig,
           private val requestQueue: RequestQueue): CavernResult<User> {

    class EmptyUsernameException: VolleyError("Username must not be empty")
    class EmptyPasswordException: VolleyError("Password must not by empty")
    class WrongCredentialException: VolleyError("Username or password is wrong")

    var account: Account = Account("", "", Role(8, "", false, false, false), "", 0, "")

    override fun get(onSuccess: (User) -> Unit, onFailure: (VolleyError) -> Unit) {
        val url = "${Cavern.host}/login.php"
        if(username == "") {
            onFailure(EmptyUsernameException())
        }
        if(password == "") {
            onFailure(EmptyPasswordException())
        }
        val request = object: CavernStringRequest(Request.Method.POST, url,
                Response.Listener {
                    val document = Jsoup.parse(it)
                    if(document.body().selectFirst(remoteConfig.getString("login")) == null) {
                        //logged in successfully
                        val userURL = "${Cavern.host}/ajax/user.php"
                        val subRequest = CavernJsonObjectRequest(Method.GET, userURL, null,
                                Response.Listener {
                                    val nickname = it.getString(remoteConfig, "author_key")
                                    val email = it.getString(remoteConfig, "email_key")
                                    val imageLink = "https://www.gravatar.com/avatar/${it.getString(remoteConfig, "hash_key")}?d=https%3A%2F%2Ftocas-ui.com%2Fassets%2Fimg%2F5e5e3a6.png&s=500"
                                    val level = it.getInt(remoteConfig, "role_key")
                                    val postCount = it.getInt(remoteConfig, "posts_count_key")
                                    RoleDetail(level).get({ detail ->
                                        account = Account(username, nickname, detail.role, imageLink, postCount, email)
                                        onSuccess(this)
                                    }) {
                                        onFailure(it)
                                    }
                                },
                                CavernErrorListener(onFailure))
                        requestQueue.add(subRequest)
                    } else {
                        onFailure(WrongCredentialException())
                    }
                },
                CavernErrorListener(onFailure)) {

            override fun getParams(): MutableMap<String, String> {
                return mutableMapOf("username" to username,
                        "password" to password)
            }
        }
        requestQueue.add(request)
    }
}