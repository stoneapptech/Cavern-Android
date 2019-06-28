package tech.stoneapp.secminhr.cavern.accountInfo.user

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_bottom_author_dialog.*
import tech.stoneapp.secminhr.cavern.R
import tech.stoneapp.secminhr.cavern.accountInfo.AccountInfoHolder
import tech.stoneapp.secminhr.cavern.activity.LoggedUserModelHolder
import tech.stoneapp.secminhr.cavern.cavernObject.Account
import tech.stoneapp.secminhr.cavern.cavernObject.Role
import tech.stoneapp.secminhr.cavern.databinding.FragmentBottomAuthorDialogBinding

class UserFragment : Fragment(), AccountInfoHolder {

    private lateinit var viewModel: UserViewModel
    override val account = ObservableField<Account>(Account("", "", Role(8, "", false, false, false), "", -1))

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentBottomAuthorDialogBinding.inflate(inflater)
        binding.holder = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)
        val sharedViewModel = (activity as LoggedUserModelHolder).loggedUserModel
        account.set(sharedViewModel.user.get()!!)
        setHasOptionsMenu(true)
        loadingProgressBar.visibility = View.GONE
        mainContentLayout.visibility = View.VISIBLE
        Picasso.get().load(account.get()!!.avatarLink).into(avatarImageView)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.user_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val selector:HashMap<Int, () -> Unit> = hashMapOf(R.id.logout_item to ::logout)
        selector[item?.itemId]?.invoke()
        return selector.keys.contains(item?.itemId)
    }

    private fun logout() {
        viewModel.logout(errorHandler = {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }) {
            (activity as LoggedUserModelHolder).loggedUserModel.user.set(null)
        }
    }

}