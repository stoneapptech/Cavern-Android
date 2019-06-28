package tech.stoneapp.secminhr.cavern.api.results

import com.android.volley.VolleyError
import com.google.firebase.functions.FirebaseFunctions
import tech.stoneapp.secminhr.cavern.cavernObject.Role

class RoleDetail(val level: Int): CavernResult<RoleDetail> {

    lateinit var role: Role

    override fun get(onSuccess: (RoleDetail) -> Unit, onFailure: (VolleyError) -> Unit) {
        val data = hashMapOf(
                "level" to level
        )
        FirebaseFunctions.getInstance()
                .getHttpsCallable("getRole")
                .call(data)
                .continueWith { task ->
                    if(task.isSuccessful) {
                        val result = task.result?.data as Map<String, Any>
                        role = Role(level, result["name"] as String,
                                result["canPostArticle"] as Boolean,
                                result["canLike"] as Boolean,
                                result["canComment"] as Boolean)
                        onSuccess(this)
                    } else {
                        onFailure(VolleyError("get role failed"))
                    }
                }
    }
}