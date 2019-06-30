package tech.stoneapp.secminhr.cavern.activity

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import stoneapp.secminhr.cavern.cavernObject.Account

class LoggedUserDataViewModel: ViewModel() {
    val user = ObservableField<Account?>()
}