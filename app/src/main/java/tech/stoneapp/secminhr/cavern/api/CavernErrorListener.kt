package tech.stoneapp.secminhr.cavern.api

import com.android.volley.Response
import com.android.volley.VolleyError

class CavernErrorListener(private val errorHandler: (VolleyError) -> Unit): Response.ErrorListener {
    override fun onErrorResponse(error: VolleyError?) {
        error?.let {
            error.printStackTrace()
            errorHandler(it)
        }
    }
}