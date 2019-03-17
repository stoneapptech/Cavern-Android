package tech.stoneapp.secminhr.cavern.api

import com.android.volley.VolleyError
import tech.stoneapp.secminhr.cavern.api.results.CavernResult

class CavernTask<R: CavernResult<R>>(private val result: R) {

    private val onSuccessListener: MutableList<(R) -> Unit> = mutableListOf()
    private val onFailureListener: MutableList<(VolleyError) -> Unit> = mutableListOf()

    fun addOnSuccessListener(listener: (R) -> Unit): CavernTask<R> {
        onSuccessListener += listener
        return this
    }

    fun addOnFailureListener(listener: (VolleyError) -> Unit): CavernTask<R> {
        onFailureListener += listener
        return this
    }

    fun execute() {
        result.get(onSuccess = {
            for(listener in onSuccessListener) {
                listener(it)
            }
        }, onFailure = {
            for(listener in onFailureListener) {
                listener(it)
            }
        })
    }
}