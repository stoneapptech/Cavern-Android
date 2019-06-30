package tech.stoneapp.secminhr.cavern.accountInfo.user

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import stoneapp.secminhr.cavern.api.Cavern
import stoneapp.secminhr.cavern.cavernError.LogoutFailedError
import stoneapp.secminhr.cavern.cavernError.NetworkError
import stoneapp.secminhr.cavern.cavernError.NoConnectionError

class UserViewModel(application: Application) : AndroidViewModel(application) {

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
                is LogoutFailedError -> {
                    it.message
                }
                else -> {
                    "Some unexpected error happened\nPlease turn off the app and try again later\nWe are sorry for that"
                }
            }
            errorMessage?.let {
                errorHandler(it)
            }
        }.execute()
    }
}
