package tech.stoneapp.secminhr.cavern.accountInfo.bottomAuthorDialog

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.NetworkError
import com.android.volley.NoConnectionError
import tech.stoneapp.secminhr.cavern.api.Cavern
import tech.stoneapp.secminhr.cavern.cavernObject.Account

class BottomAuthorDialogViewModel(application: Application): AndroidViewModel(application) {

    var author = MutableLiveData<Account>()
//    val requestQueue = getApplication<CavernApplication>().requestQueue

    fun showUser(username: String, errorHandler: (String) -> Unit): LiveData<Account> {
        author = MutableLiveData()
        Cavern.getInstance(getApplication()).getAuthor(username).addOnSuccessListener {
            author.postValue(it.account)
        }.addOnFailureListener {
            var errorMessage = when(it) {
                is NetworkError -> "There's something wrong with the server\nPlease try again later"
                is NoConnectionError -> "Your device seems to be offline\nPlease turn on the internet connection and try again"
                else -> "Some unexpected error happened\nPlease turn off the app and try again later\nWe are sorry for that"
            }
            if(it.networkResponse.statusCode == 404) {
                errorMessage = "Author doesn't exist"
            }
            errorHandler(errorMessage)
        }.execute()
        return author
    }
}