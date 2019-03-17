package tech.stoneapp.secminhr.cavern

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import org.json.JSONArray
import org.json.JSONObject

fun JSONObject.getString(config: FirebaseRemoteConfig, key: String): String = this.getString(config.getString(key))
fun JSONObject.getInt(config: FirebaseRemoteConfig, key: String) = getInt(config.getString(key))
fun JSONObject.getJSONArray(config: FirebaseRemoteConfig, key: String): JSONArray = getJSONArray(config.getString(key))
fun JSONObject.getBoolean(config: FirebaseRemoteConfig, key: String): Boolean = getBoolean(config.getString(key))
fun JSONObject.getJSONObject(config: FirebaseRemoteConfig, key: String): JSONObject = getJSONObject(config.getString(key))
fun JSONObject.get(config: FirebaseRemoteConfig, key: String): Any = get(config.getString(key))

fun String?.isNotNullNorEmpty() = !this.isNullOrEmpty()