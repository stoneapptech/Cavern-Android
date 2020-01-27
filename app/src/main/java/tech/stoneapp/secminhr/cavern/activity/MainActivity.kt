package tech.stoneapp.secminhr.cavern.activity

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.activity_main.*
import stoneapp.secminhr.cavern.api.Cavern
import tech.stoneapp.secminhr.cavern.BuildConfig
import tech.stoneapp.secminhr.cavern.R
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), LoggedUserModelHolder {

    override lateinit var loggedUserModel: LoggedUserDataViewModel
    var hasUserLoggedIn = ObservableBoolean(false)

    private val onUserChangedListener = object: Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            runOnUiThread {
                hasUserLoggedIn.set(loggedUserModel.user.get() != null)
                navigation.menu.findItem(R.id.navigation_user).isVisible = hasUserLoggedIn.get()
                navigation.menu.findItem(R.id.navigation_login).isVisible = !hasUserLoggedIn.get()
                navigation.selectedItemId =
                        if(hasUserLoggedIn.get()) R.id.navigation_user else R.id.navigation_login
                invalidateOptionsMenu()

                PreferenceManager.getDefaultSharedPreferences(this@MainActivity).edit {
                    putBoolean("article_outdated", true)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val configFinished = intent.getBooleanExtra("remoteConfig", false)
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val isConnected = cm.activeNetworkInfo?.isConnected ?: false
        if(!isConnected) {
            setContentView(R.layout.activity_main_no_connection)
        } else {
            if(!configFinished) {
                val remoteConfig = FirebaseRemoteConfig.getInstance()
                val expireTime = if(BuildConfig.DEBUG) 0L else 43200L
                remoteConfig.fetch(expireTime).addOnCompleteListener {
                    if (it.isSuccessful) {
                        remoteConfig.activate()
                        showContent()
                    }
                }
            } else {
                showContent()
            }
        }
    }

    private fun showContent() {
        setContentView(R.layout.activity_main)
        NavigationUI.setupWithNavController(navigation, container.findNavController())

        loggedUserModel = ViewModelProviders.of(this).get(LoggedUserDataViewModel::class.java)
        thread {
            Cavern.getInstance(this).login().addOnSuccessListener {
                hasUserLoggedIn.set(true)
                loggedUserModel.user.set(it.account)
                navigation.menu.findItem(R.id.navigation_user).isVisible = true
                navigation.menu.findItem(R.id.navigation_login).isVisible = false
                invalidateOptionsMenu()
                loggedUserModel.user.addOnPropertyChangedCallback(onUserChangedListener)
            }.addOnFailureListener {
                loggedUserModel.user.addOnPropertyChangedCallback(onUserChangedListener)
            }.execute()
        }
        setSupportActionBar(toolbar)
    }
}
