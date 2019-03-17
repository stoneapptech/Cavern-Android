package tech.stoneapp.secminhr.cavern.accountInfo

import androidx.databinding.ObservableField
import tech.stoneapp.secminhr.cavern.cavernObject.Account

interface AccountInfoHolder {
    val account: ObservableField<Account>
}