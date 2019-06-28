package tech.stoneapp.secminhr.cavern.api.results

import com.android.volley.*
import tech.stoneapp.secminhr.cavern.api.Cavern
import tech.stoneapp.secminhr.cavern.api.CavernErrorListener
import tech.stoneapp.secminhr.cavern.cavernService.CavernStringRequest

class LogoutResult(private val queue: RequestQueue): CavernResult<LogoutResult> {

    class LogoutFailedException: VolleyError("Couldn't logout\nSome unexpected error happened")

    override fun get(onSuccess: (LogoutResult) -> Unit, onFailure: (VolleyError) -> Unit) {
        val url = "${Cavern.host}/login.php?logout"
        val request = object: CavernStringRequest(Request.Method.GET, url, Response.Listener {
        }, CavernErrorListener(onFailure)) {
            override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
                val location = response?.headers?.get("axios-location")
                location?.let {
                    if(it == "index.php?ok=logout") {
                        onSuccess(this@LogoutResult)
                    } else {
                        onFailure(LogoutFailedException())
                    }
                }
                return super.parseNetworkResponse(response)
            }
        }
        queue.add(request)
    }
}