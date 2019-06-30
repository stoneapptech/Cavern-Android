package tech.stoneapp.secminhr.cavern.accountInfo.bottomAuthorDialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_bottom_author_dialog.*
import stoneapp.secminhr.cavern.cavernObject.Account
import stoneapp.secminhr.cavern.cavernObject.Role
import tech.stoneapp.secminhr.cavern.accountInfo.AccountInfoHolder
import tech.stoneapp.secminhr.cavern.databinding.FragmentBottomAuthorDialogBinding

@SuppressLint("ValidFragment")
class BottomAuthorDialog : BottomSheetDialogFragment, AccountInfoHolder {

    val username: String
    private constructor(username: String) {
        this.username = username
    }
    lateinit var binding: FragmentBottomAuthorDialogBinding
    lateinit var viewModel: BottomAuthorDialogViewModel
    override var account = ObservableField<Account>(Account("", "", Role(8, "", false, false, false), "", -1))

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentBottomAuthorDialogBinding.inflate(inflater)
        binding.holder = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(BottomAuthorDialogViewModel::class.java)
        viewModel.showUser(username) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }.observe(this, Observer {
            account.set(it)
            Picasso.get().load(it.avatarLink).into(avatarImageView)
            loadingProgressBar.visibility = View.GONE
            mainContentLayout.visibility = View.VISIBLE
        })
    }

    companion object {
        fun newInstance(username: String) = BottomAuthorDialog(username)
    }
}
