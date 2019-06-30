package tech.stoneapp.secminhr.cavern.accountInfo.bottomAuthorDialog

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import stoneapp.secminhr.cavern.api.Cavern
import stoneapp.secminhr.cavern.cavernError.NetworkError
import stoneapp.secminhr.cavern.cavernError.NoConnectionError
import stoneapp.secminhr.cavern.cavernError.NotExistsError
import stoneapp.secminhr.cavern.cavernObject.Account

class BottomAuthorDialogViewModel(application: Application): AndroidViewModel(application) {

    var author = MutableLiveData<Account>()

    fun showUser(username: String, errorHandler: (String) -> Unit): LiveData<Account> {
        author = MutableLiveData()
        Cavern.getInstance(getApplication()).getAuthor(username).addOnSuccessListener {
            author.postValue(it.account)
        }.addOnFailureListener {
            val errorMessage = when(it) {
                is NetworkError -> "There's something wrong with the server\nPlease try again later"
                is NoConnectionError -> "Your device seems to be offline\nPlease turn on the internet connection and try again"
                is NotExistsError -> "Author doesn't exist"
                else -> "Some unexpected error happened\nPlease turn off the app and try again later\nWe are sorry for that"
            }
            errorHandler(errorMessage)
        }.execute()
        return author
    }
}