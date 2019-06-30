package tech.stoneapp.secminhr.cavern

import androidx.multidex.MultiDexApplication
import stoneapp.secminhr.cavern.cavernService.CavernCookieStore
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import kotlin.concurrent.thread

class CavernApplication: MultiDexApplication() {

    lateinit var cookieStore: CavernCookieStore

    override fun onCreate() {
        super.onCreate()
        thread {
            cookieStore = CavernCookieStore(this)
            CookieHandler.setDefault(CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL))
        }
    }
}