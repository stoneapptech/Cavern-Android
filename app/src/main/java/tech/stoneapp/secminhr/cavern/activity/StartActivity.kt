package tech.stoneapp.secminhr.cavern.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
            checkUpdate(followingStep = {
                val remoteConfig = FirebaseRemoteConfig.getInstance()
                val firestore = FirebaseFirestore.getInstance()
                remoteConfig.setDefaults(R.xml.selector_default)
                val remoteSetting = FirebaseRemoteConfigSettings.Builder()
                if(BuildConfig.DEBUG) {
                    remoteSetting.setMinimumFetchIntervalInSeconds(3L)
                }
                remoteConfig.setConfigSettingsAsync(remoteSetting.build())
                var expireTime = 43200L
                if (BuildConfig.DEBUG) {
                    expireTime = 0L
                }
                val selectorVersionDocument = firestore.collection("app-meta").document("selector-version")
                selectorVersionDocument.get().addOnSuccessListener { doc ->
                    if (doc.getLong("version")!!.toInt() >
                            PreferenceManager.getDefaultSharedPreferences(application).getInt("version", 0)) {
                        expireTime = 0L
                    }
                    remoteConfig.fetch(expireTime).addOnCompleteListener {
                        if (it.isSuccessful) {
                            remoteConfig.activate()
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
            })
        }, 200)
    }

    private fun checkUpdate(followingStep: () -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        val appVersionDocument = firestore.collection("app-meta").document("newest-version")
        appVersionDocument.get().addOnSuccessListener { snapshot ->
            val newest = snapshot.getLong("version")!!
            val needUpdate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                newest > packageManager.getPackageInfo(packageName, 0).longVersionCode
            } else {
                newest.toInt() > packageManager.getPackageInfo(packageName, 0).versionCode
            }
            if(needUpdate) {
                Log.e("Start", "need update")
                AlertDialog.Builder(this)
                        .setTitle(R.string.update_title)
                        .setMessage(R.string.update_message)
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse("https://github.com/stoneapptech/Cavern-Android/releases")
                            startActivity(intent)
                        }
                        .setNegativeButton(android.R.string.no) { _, _ ->
                            Toast.makeText(this, R.string.update_reject_message, Toast.LENGTH_LONG).show()
                            finish()
                        }
                        .show()
            } else {
                followingStep()
            }
        }
    }
}
