package tech.stoneapp.secminhr.cavern.accountInfo.user

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.android.volley.NetworkError
import com.android.volley.NoConnectionError
import tech.stoneapp.secminhr.cavern.api.Cavern
import tech.stoneapp.secminhr.cavern.api.results.LogoutResult

class UserViewModel(application: Application) : AndroidViewModel(application) {

//    val requestQueue: RequestQueue = getApplication<CavernApplication>().requestQueue

    fun logout(errorHandler: (String) -> Unit, onSuccess: () -> Unit) {
        Cavern.getInstance(getApplication()).logout().addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            val errorMessage = when(it) {
                is NetworkError -> {
                    "There's something wrong with the server\nPlease try again later"
                }
                is NoConnectionError -> {
                    "Your device seems to be offline\nPlease turn on the internet connection and try again"
                }
                is LogoutResult.LogoutFailedException -> {
                    it.message!!
                }
                else -> {
                    "Some unexpected error happened\nPlease turn off the app and try again later\nWe are sorry for that"
                }
            }
            errorHandler(errorMessage)
        }.execute()
    }
}
