package tech.stoneapp.secminhr.cavern.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.login_fragment.*
import tech.stoneapp.secminhr.cavern.activity.LoggedUserModelHolder
import tech.stoneapp.secminhr.cavern.api.results.User
import tech.stoneapp.secminhr.cavern.cavernObject.Account
import tech.stoneapp.secminhr.cavern.databinding.LoginFragmentBinding

class LoginFragment: Fragment() {

    val username = ObservableField<String>("")
    val password = ObservableField<String>("")
    lateinit var viewModel: LoginViewModel
    val isLoggingIn = ObservableBoolean(false)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(activity!!).get(LoginViewModel::class.java)
        val binding = LoginFragmentBinding.inflate(inflater, container, false)
        binding.fragment = this
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.loginWithSession().observe(this, Observer {
            showAccountPage(it, activity as LoggedUserModelHolder)
        })
    }

    override fun onResume() {
        super.onResume()
    }

    fun onLoginClicked(view: View) {
        login(username.get()!!, password.get()!!)
    }

    private fun login(username: String, password: String) {
        isLoggingIn.set(true)
        usernameInput.isErrorEnabled = false
        passwordInput.isErrorEnabled = false
        try {
            viewModel.login(username, password) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                isLoggingIn.set(false)
            }.observe(this, Observer {
                showAccountPage(it, activity as LoggedUserModelHolder)
            })
        } catch (e: User.EmptyUsernameException) {
            usernameInput.error = e.message
            isLoggingIn.set(false)
        } catch (e: User.EmptyPasswordException) {
            passwordInput.error = e.message
            isLoggingIn.set(false)
        }
    }

    private fun showAccountPage(account: Account, modelHolder: LoggedUserModelHolder) {
        val userDataViewModel = modelHolder.loggedUserModel
        userDataViewModel.user.set(account)
    }
}