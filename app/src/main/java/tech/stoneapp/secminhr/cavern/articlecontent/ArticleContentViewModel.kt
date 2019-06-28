package tech.stoneapp.secminhr.cavern.articlecontent

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.AuthFailureError
import com.android.volley.NetworkError
import com.android.volley.NoConnectionError
import tech.stoneapp.secminhr.cavern.api.Cavern
import tech.stoneapp.secminhr.cavern.api.results.ArticleContent
import tech.stoneapp.secminhr.cavern.api.results.LikeResult
import tech.stoneapp.secminhr.cavern.cavernObject.Account
import tech.stoneapp.secminhr.cavern.cavernObject.Comment
import kotlin.concurrent.thread

class ArticleContentViewModel(application: Application): AndroidViewModel(application) {
    var content = MutableLiveData<ArticleContent>()
    var author = MutableLiveData<Account>()
    var comments = MutableLiveData<Array<Comment>>()

    fun getArticleContent(id: Int, errorHandler: (String) -> Unit): LiveData<ArticleContent> {
        content = MutableLiveData()
        thread {
            Cavern.getInstance(getApplication()).getArticleContent(id).addOnSuccessListener {
                this.content.postValue(it)
            }.addOnFailureListener {
                val errorMessage = when (it) {
                    is NetworkError -> "There's something wrong with the server\nPlease try again later"
                    is NoConnectionError -> "Your device seems to be offline\nPlease turn on the internet connection and try again"
                    is ArticleContent.ContentNotExistsError -> "Content not available"
                    else -> "Some unexpected error happened\nPlease turn off the app and try again later\nWe are sorry for that"
                }
                errorHandler(errorMessage)
            }.execute()
        }
        return content
    }

    fun getAuthor(username: String, errorHandler: (String) -> Unit): LiveData<Account> {
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

    fun getComments(id: Int, errorHandler: (String) -> Unit): LiveData<Array<Comment>> {
        comments = MutableLiveData()
        thread {
            Cavern.getInstance(getApplication()).getComments(id).addOnSuccessListener {
                comments.postValue(it.comments.toTypedArray())
            }.addOnFailureListener {
                val errorMessage = when (it) {
                    is NetworkError -> "There's something wrong with the server\nPlease try again later"
                    is NoConnectionError -> "Your device seems to be offline\nPlease turn on the internet connection and try again"
                    else -> "Some unexpected error happened\nPlease turn off the app and try again later\nWe are sorry for that"
                }
                errorHandler(errorMessage)
            }.execute()
        }
        return comments
    }

    fun like(id: Int, errorHandler: (String) -> Unit, onSuccess: (Boolean) -> Unit) {
        thread {
            Cavern.getInstance(getApplication()).like(id).addOnSuccessListener {
                onSuccess(it.isLiked)
            }.addOnFailureListener {
                val errorMessage = when (it) {
                    is NetworkError -> "There's something wrong with the server\nPlease try again later"
                    is NoConnectionError -> "Your device seems to be offline\nPlease turn on the internet connection and try again"
                    is LikeResult.NoLoginError -> null
                    is AuthFailureError -> null //hasn't logged in
                    else -> "Some unexpected error happened\nPlease turn off the app and try again later\nWe are sorry for that"
                }
                errorMessage?.let {
                    errorHandler(it)
                }
            }.execute()
        }
    }
}