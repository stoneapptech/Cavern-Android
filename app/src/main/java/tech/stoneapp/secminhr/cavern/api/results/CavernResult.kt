package tech.stoneapp.secminhr.cavern.api.results

import com.android.volley.VolleyError

interface CavernResult<R: CavernResult<R>> {
    fun get(onSuccess: (R) -> Unit, onFailure: (VolleyError) -> Unit)
}