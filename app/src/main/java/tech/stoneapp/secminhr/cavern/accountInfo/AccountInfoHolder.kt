package tech.stoneapp.secminhr.cavern.accountInfo

import androidx.databinding.ObservableField
import stoneapp.secminhr.cavern.cavernObject.Account

interface AccountInfoHolder {
    val account: ObservableField<Account>
}