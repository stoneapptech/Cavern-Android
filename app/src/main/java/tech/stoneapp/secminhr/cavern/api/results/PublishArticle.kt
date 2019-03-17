package tech.stoneapp.secminhr.cavern.api.results

import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import tech.stoneapp.secminhr.cavern.api.CavernErrorListener
import tech.stoneapp.secminhr.cavern.cavernService.CavernStringRequest

class PublishArticle(val title: String, val content: String, val requestQueue: RequestQueue): CavernResult<PublishArticle> {
    override fun get(onSuccess: (PublishArticle) -> Unit, onFailure: (VolleyError) -> Unit) {
        val url = "https://stoneapp.tech/cavern/post.php"
        val request = object: CavernStringRequest(Method.POST, url, Response.Listener {
            Log.e("Publish", it)
            onSuccess(this)
        }, CavernErrorListener(onFailure)) {
            override fun getParams(): MutableMap<String, String> {
                return mutableMapOf("title" to title,
                                    "content" to content,
                                    "pid" to "-1")
            }
        }
        requestQueue.add(request)
    }
}