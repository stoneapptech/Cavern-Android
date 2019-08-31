package tech.stoneapp.secminhr.cavern.articles

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import stoneapp.secminhr.cavern.api.Cavern
import stoneapp.secminhr.cavern.cavernError.NetworkError
import stoneapp.secminhr.cavern.cavernError.NoConnectionError
import stoneapp.secminhr.cavern.cavernError.NoLoginError
import stoneapp.secminhr.cavern.cavernObject.ArticlePreview

class ArticleViewModel(application: Application): AndroidViewModel(application) {
    var firstVisible:Int = 0
    var top: Int = 0

    fun getArticle(): LiveData<PagedList<ArticlePreview>> {
        val factory = ArticlesDataSourceFactory(getApplication())
        return factory.toLiveData(
                20
        )
    }

//    fun getArticles(requireLoad: Boolean = false, errorHandler: (String) -> Unit): LiveData<PagedList<ArticlePreview>> {
//        if(requireLoad ||
//                articles.value?.isEmpty() != false ||
//                PreferenceManager.getDefaultSharedPreferences(getApplication()).getBoolean("article_outdated", false)) {
//            articles = MutableLiveData()
//            Cavern.getInstance(getApplication()).getArticles().addOnSuccessListener {
//                PreferenceManager.getDefaultSharedPreferences(getApplication()).edit {
//                    putBoolean("article_outdated", false)
//                }
//                articles.postValue(it.articles.toTypedArray())
//            }.addOnFailureListener {
//                val errorMessage = when(it) {
//                    is NetworkError -> "There's something wrong with the server\nPlease try again later"
//                    is NoConnectionError -> "Your device seems to be offline\nPlease turn on the internet connection and try again"
//                    else -> "Some unexpected error happened\nPlease turn off the app and try again later\nWe are sorry for that"
//                }
//                errorHandler(errorMessage)
//            }.execute()
//        } else {
//            articles.postValue(articles.value!!)
//        }
//        Log.e("ViewModel", "before return articles")
//        return articles
//    }

    fun like(id: Int, errorHandler: (String) -> Unit, onSuccess: (Boolean, Int) -> Unit) {
        Cavern.getInstance(getApplication()).like(id).addOnSuccessListener {
            onSuccess(it.isLiked, it.likeCount)
        }.addOnFailureListener {
            val errorMessage = when(it) {
                is NetworkError -> "There's something wrong with the server\nPlease try again later"
                is NoConnectionError -> "Your device seems to be offline\nPlease turn on the internet connection and try again"
                is NoLoginError -> "You haven't logged in\nPlease login first"
                else -> "Some unexpected error happened\nPlease turn off the app and try again later\nWe are sorry for that"
            }
            errorHandler(errorMessage)
        }.execute()
    }
}