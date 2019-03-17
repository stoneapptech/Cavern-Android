package tech.stoneapp.secminhr.cavern

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingConversion
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

object Converters {
    @JvmStatic
    @BindingConversion
    fun convertDateToString(date: Date): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.US).format(date)
    }
}

@BindingAdapter("imageURL")
fun loadImage(view: ImageView, url: String) {
    Picasso.get().load(url).into(view)
}