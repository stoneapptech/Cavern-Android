package tech.stoneapp.secminhr.cavern.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.NetworkError
import com.android.volley.NoConnectionError
import tech.stoneapp.secminhr.cavern.api.Cavern
import tech.stoneapp.secminhr.cavern.api.results.User
import tech.stoneapp.secminhr.cavern.cavernObject.Account



class LoginViewModel(application: Application): AndroidViewModel(application) {
    var loggedUser: MutableLiveData<Account> = MutableLiveData()
//    private val requestQueue = getApplication<CavernApplication>().requestQueue

    fun loginWithSession(): LiveData<Account> {
        loggedUser = MutableLiveData()
        Cavern.getInstance(getApplication()).login().addOnSuccessListener {
            loggedUser.postValue(it.account)
        }.execute()
        return loggedUser
    }

    fun login(username: String, password: String, errorHandler: (String) -> Unit): LiveData<Account> {
        Cavern.getInstance(getApplication()).login(username, password).addOnSuccessListener {
            loggedUser.postValue(it.account)
        }.addOnFailureListener {
            val errorMessage:String
            when(it) {
                is NetworkError -> {
                    errorMessage = "There's something wrong with the server\nPlease try again later"
                }
                is NoConnectionError -> {
                    errorMessage = "Your device seems to be offline\nPlease turn on the internet connection and try again"
                }
                is User.EmptyUsernameException -> {
                    throw it
                }
                is User.EmptyPasswordException -> {
                    throw it
                }
                else -> {
                    errorMessage = "Some unexpected error happened\nPlease turn off the app and try again later\nWe are sorry for that"
                }
            }
            errorHandler(errorMessage)
        }.execute()
        return loggedUser
    }
}