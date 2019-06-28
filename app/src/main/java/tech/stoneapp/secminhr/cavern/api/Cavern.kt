package tech.stoneapp.secminhr.cavern.api

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import tech.stoneapp.secminhr.cavern.api.results.*

class Cavern private constructor(private val requestQueue: RequestQueue) {

    private val remoteConfig = FirebaseRemoteConfig.getInstance()


    fun getArticles(): CavernTask<Articles> {
        return CavernTask(Articles(remoteConfig, requestQueue))
    }

    fun getArticleContent(id: Int): CavernTask<ArticleContent> {
        return CavernTask(ArticleContent(id, remoteConfig, requestQueue))
    }

    fun getAuthor(username: String): CavernTask<Author> {
        return CavernTask(Author(username, remoteConfig, requestQueue))
    }

    fun getComments(id: Int): CavernTask<Comments> {
        return CavernTask(Comments(id, remoteConfig, requestQueue))
    }

    fun login(username: String, password: String): CavernTask<User> {
        return CavernTask(User(username, password, remoteConfig, requestQueue))
    }

    fun login(): CavernTask<User> {
        return CavernTask(SessionUser(remoteConfig, requestQueue))
    }

    fun logout(): CavernTask<LogoutResult> {
        return CavernTask(LogoutResult(requestQueue))
    }

    fun like(id: Int): CavernTask<LikeResult> {
        return CavernTask(LikeResult(id, remoteConfig, requestQueue))
    }

    fun publishArticle(title: String, content: String): CavernTask<PublishArticle> {
        return CavernTask(PublishArticle(title, content, requestQueue))
    }

    fun getRoleDetail(level: Int): CavernTask<RoleDetail> {
        return CavernTask(RoleDetail(level))
    }

    companion object {
        private var instance: Cavern? = null
        fun getInstance(context: Context) =
                instance ?: Cavern(Volley.newRequestQueue(context)).also { instance = it }

        val host = "https://stoneapp.tech/cavern"
    }
}