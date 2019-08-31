package tech.stoneapp.secminhr.cavern

import android.graphics.Rect
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.zzhoujay.richtext.ImageHolder
import com.zzhoujay.richtext.RichTextConfig
import com.zzhoujay.richtext.callback.DrawableGetter

class ImagePlaceHolder: DrawableGetter {
    override fun getDrawable(holder: ImageHolder?, config: RichTextConfig?, textView: TextView?): Drawable {
        Log.e("ImagePlaceHolder", "in getDrawable")
        val drawable = ResourcesCompat.getDrawable(textView!!.resources, R.drawable.loading_image_placeholder, null)!!
        drawable.bounds = Rect(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Looper.prepare()
            Handler().postDelayed({
                (drawable as AnimatedVectorDrawable).start()
            }, 100)
        }
        return drawable
    }
}