package stoneapp.secminhr.cavern.test

import androidx.databinding.ObservableInt
import com.android.volley.NetworkResponse
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import org.json.JSONObject
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers.any
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import stoneapp.secminhr.cavern.api.results.ArticleContent
import stoneapp.secminhr.cavern.api.results.Articles
import stoneapp.secminhr.cavern.cavernError.NotExistsError
import stoneapp.secminhr.cavern.cavernObject.ArticlePreview
import stoneapp.secminhr.cavern.cavernService.CavernJsonObjectRequest
import java.text.SimpleDateFormat
import java.util.*

class CavernApiUnitTest {

    @Mock
    lateinit var config: FirebaseRemoteConfig
    @Mock
    lateinit var queue: RequestQueue

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        //mocking remote config
        `when`(config.getString("markdown_key")).thenReturn("markdown")
        `when`(config.getString("id_key")).thenReturn("id")
        `when`(config.getString("names_key")).thenReturn("names")
        `when`(config.getString("comments_key")).thenReturn("comments")
        `when`(config.getString("likes_key")).thenReturn("likes")
        `when`(config.getString("status_key")).thenReturn("status")
        `when`(config.getString("username_key")).thenReturn("username")
        `when`(config.getString("author_username_key")).thenReturn("author")
        `when`(config.getString("posts_count_key")).thenReturn("posts_count")
        `when`(config.getString("role_key")).thenReturn("level")
        `when`(config.getString("login_key")).thenReturn("login")
        `when`(config.getString("hash_key")).thenReturn("hash")
        `when`(config.getString("email_key")).thenReturn("email")
        `when`(config.getString("message_key")).thenReturn("message")
        `when`(config.getString("content_key")).thenReturn("content")
        `when`(config.getString("posts_key")).thenReturn("posts")
        `when`(config.getString("post_key")).thenReturn("post")
        `when`(config.getString("is_liked")).thenReturn("islike")
        `when`(config.getString("total_posts_num")).thenReturn("all_posts_count")
        `when`(config.getString("page_limit")).thenReturn("page_limit")
        `when`(config.getString("comments_number_key")).thenReturn("comments_count")
        `when`(config.getString("pid_key")).thenReturn("pid")
        `when`(config.getString("upvote_key")).thenReturn("likes_count")
        `when`(config.getString("date_key")).thenReturn("time")
        `when`(config.getString("author_key")).thenReturn("name")
        `when`(config.getString("title_key")).thenReturn("title")
    }

    //Tests on ArticleContentResult
    //valid id
    @Test
    fun articleContentResultValidID() {
        val task = ArticleContent(100, config, queue)
        `when`(queue.add(any(CavernJsonObjectRequest::class.java))).thenAnswer {
            val request = it.arguments[0] as CavernJsonObjectRequest
            val subJson = JSONObject()
            subJson.put("author", "AuthorTest")
            subJson.put("name", "AuthorName")
            subJson.put("title", "Test")
            subJson.put("content", "testContent")
            subJson.put("time", "2018-03-12 17:08:02")
            subJson.put("likes_count", 2)
            subJson.put("comments_count", 1)
            subJson.put("islike", false)
            val json = JSONObject()
            json.put("fetch", 1234583)
            json.put("post", subJson)
            request.listener.onResponse(json)
            request
        }

        task.get(onSuccess = {
            Assert.assertEquals("AuthorTest", it.authorUsername)
            Assert.assertEquals("AuthorName", it.authorNickname)
            Assert.assertEquals("Test", it.title)
            Assert.assertEquals("testContent", it.content)
            Assert.assertEquals(false, it.isLiked)
        }) {
            Assert.fail()
        }
    }

    //invalid id
    @Test
    fun articleContentResultInvalidID() {
        val task = ArticleContent(100, config, queue)
        val data = "{\"fetch\":156207579882, \"message\": \"There is no post with pid 100\"}".toByteArray()
        val response = NetworkResponse(404, data, false, 500L, null)
        `when`(queue.add(any(CavernJsonObjectRequest::class.java))).thenAnswer {
            val request = it.arguments[0] as CavernJsonObjectRequest
            request.errorListener!!.onErrorResponse(VolleyError(response))
            request
        }

        task.get(onSuccess = {
            Assert.fail()
        }) {
            Assert.assertTrue(it is NotExistsError)
        }
    }

    //only one page
    @Test
    fun articlesResultOnePage() {
        val task = Articles(config, queue, 1, 10)
        `when`(queue.add(any(CavernJsonObjectRequest::class.java))).thenAnswer {
            val request = it.arguments[0] as CavernJsonObjectRequest
            val json = when(request.url) {
                "https://stoneapp.tech/cavern/ajax/posts.php?page=1&limit=10" -> {
                    JSONObject("{\"fetch\":1562134491625,\"page_limit\":10,\"page\":1,\"all_posts_count\":1,\"posts\":[{\"author\":\"secminhr\",\"name\":\"secminhr\",\"pid\":646,\"title\":\"Cavern\",\"time\":\"2019-07-02 08:33:01\",\"likes_count\":\"0\",\"comments_count\":\"1\",\"islike\":false}]}")
                }
                else -> {
                    Assert.fail()
                }
            }
            request.listener.onResponse(json as JSONObject)
            request
        }

        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val answer = ArticlePreview("Cavern", "secminhr", format.parse("2019-07-02 08:33:01"), ObservableInt(0), 646, false)

        task.get(onSuccess = {
            Assert.assertEquals(answer.title, it.articles[0].title)
            Assert.assertEquals(answer.author, it.articles[0].author)
            Assert.assertEquals(answer.date, it.articles[0].date)
            Assert.assertEquals(answer.id, it.articles[0].id)
            Assert.assertEquals(answer.liked, it.articles[0].liked)
            Assert.assertEquals(answer.upvote.get(), it.articles[0].upvote.get())
        }) {
            Assert.fail()
        }
    }





}
