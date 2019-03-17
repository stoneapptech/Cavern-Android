package tech.stoneapp.secminhr.cavern.activity

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import kotlinx.android.synthetic.main.activity_main.*
import tech.stoneapp.secminhr.cavern.R
import tech.stoneapp.secminhr.cavern.api.Cavern
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
                if(hasUserLoggedIn.get())
                    R.id.navigation_user
                else R.id.navigation_login
                invalidateOptionsMenu()

                PreferenceManager.getDefaultSharedPreferences(this@MainActivity).edit {
                    putBoolean("article_outdated", true)
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
