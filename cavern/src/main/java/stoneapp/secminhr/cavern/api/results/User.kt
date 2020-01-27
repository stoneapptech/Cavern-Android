package stoneapp.secminhr.cavern.api.results

import com.android.volley.*
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import org.jsoup.Jsoup
import stoneapp.secminhr.cavern.api.Cavern
import stoneapp.secminhr.cavern.cavernError.CavernError
import stoneapp.secminhr.cavern.cavernError.EmptyPasswordError
import stoneapp.secminhr.cavern.cavernError.EmptyUsernameError
import stoneapp.secminhr.cavern.cavernError.WrongCredentialError
import stoneapp.secminhr.cavern.cavernObject.Account
import stoneapp.secminhr.cavern.cavernObject.Role
import stoneapp.secminhr.cavern.cavernService.CavernJsonObjectRequest
import stoneapp.secminhr.cavern.cavernService.CavernStringRequest
import stoneapp.secminhr.cavern.getInt
import stoneapp.secminhr.cavern.getString
import java.util.*

open class User(val username: String,
           private val password: String,
           private val remoteConfig: FirebaseRemoteConfig,
           private val requestQueue: RequestQueue): CavernResult<User> {

    var account = Account("", "", Role(8, "", false, false, false), "", 0, "")
    lateinit var customToken: String

    override fun get(onSuccess: (User) -> Unit, onFailure: (CavernError) -> Unit) {
        val url = "${Cavern.host}/login.php"
        if(username == "") {
            onFailure(EmptyUsernameError())
        }
        if(password == "") {
            onFailure(EmptyPasswordError())
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

//                                    //firebase auth
//                                    val customUid = createCustomUid()
//                                    val data = hashMapOf("uid" to customUid)
//                                    FirebaseFunctions.getInstance()
//                                            .getHttpsCallable("createCustomToken")
//                                            .call(data)
//                                            .continueWith {
//                                                val result = it.result?.data as Map<String, String>
//                                                customToken = result["token"]!!
//                                            }


                                })
                        requestQueue.add(subRequest)
                    } else {
                        onFailure(WrongCredentialError())
                    }
                },
                Response.ErrorListener {
                    when(it) {
                        is NoConnectionError -> onFailure(stoneapp.secminhr.cavern.cavernError.NoConnectionError())
                        is NetworkError -> onFailure(stoneapp.secminhr.cavern.cavernError.NetworkError())
                    }
                }) {

            override fun getParams(): MutableMap<String, String> {
                return mutableMapOf("username" to username,
                        "password" to password)
            }
        }
        requestQueue.add(request)
    }

    private fun createCustomUid(): String {
        return UUID.randomUUID().toString()
    }

    private fun getAccountInfo(complition: (Account?) -> Unit) {
        val userURL = "${Cavern.host}/ajax/user.php"
        val subRequest = CavernJsonObjectRequest(Request.Method.GET, userURL, null,
                Response.Listener {
                    val nickname = it.getString(remoteConfig, "author_key")
                    val email = it.getString(remoteConfig, "email_key")
                    val imageLink = "https://www.gravatar.com/avatar/${it.getString(remoteConfig, "hash_key")}?d=https%3A%2F%2Ftocas-ui.com%2Fassets%2Fimg%2F5e5e3a6.png&s=500"
                    val level = it.getInt(remoteConfig, "role_key")
                    val postCount = it.getInt(remoteConfig, "posts_count_key")
                    RoleDetail(level).get({ detail ->
                        complition(Account(username, nickname, detail.role, imageLink, postCount, email))
                    }) {
                        complition(null)
                    }
                }
        )
        requestQueue.add(subRequest)
    }
}