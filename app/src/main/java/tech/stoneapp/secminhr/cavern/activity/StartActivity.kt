package tech.stoneapp.secminhr.cavern.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import tech.stoneapp.secminhr.cavern.BuildConfig
import tech.stoneapp.secminhr.cavern.R

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        Handler().postDelayed({
            val remoteConfig = FirebaseRemoteConfig.getInstance()
            val firestore = FirebaseFirestore.getInstance()
            remoteConfig.setDefaults(R.xml.selector_default)
            val remoteSetting = FirebaseRemoteConfigSettings.Builder()
                    .setDeveloperModeEnabled(BuildConfig.DEBUG).build()
            remoteConfig.setConfigSettings(remoteSetting)
            var expireTime = 43200L
            if (BuildConfig.DEBUG) {
                expireTime = 0L
            }
            val versionDocument = firestore.collection("app-meta").document("selector-version")
            versionDocument.get().addOnSuccessListener { doc ->
                if (doc.getLong("version")!!.toInt() >
                        PreferenceManager.getDefaultSharedPreferences(application).getInt("version", 0)) {
                    expireTime = 0L
                }
                remoteConfig.fetch(expireTime).addOnCompleteListener {
                    if (it.isSuccessful) {
                        remoteConfig.activateFetched()
                        Log.e("StartActivity", "before main")
                        startActivity(Intent(this@StartActivity, MainActivity::class.java))
                    } else {
                        Log.e("application", it.exception.toString())
                        it.exception?.let { _ ->
                            Toast.makeText(this@StartActivity, "We are not able to get info online, please connect your device to the internet", Toast.LENGTH_LONG).show()
                        }
                    }
                    finish()
                }
            }
        }, 500)
    }
}
