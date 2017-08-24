package com.seta.setall.steam.extensions

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.seta.setall.R
import com.seta.setall.common.extensions.Preference
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.api.models.AppRestoredBean

/**
 * Created by SETA_WORK on 2017/7/5.
 */
object DelegateSteam {
    fun <T> steamPreference(context: Context, name: String, default: T) = Preference(SteamConstants.STEAM_PRF_NAME, context, name, default)
}

fun ImageView.loadImg(imgPath: String?) {
    Glide.with(context)
            .load(imgPath)
//            .placeholder(R.mipmap.img_loading)
            .thumbnail(Glide.with(context).load(R.mipmap.img_loading).centerCrop())
            .error(R.mipmap.img_fail)
            .into(this)
}

fun ImageView.loadImg(imgResId: Int?) {
    Glide.with(context)
            .load(imgResId)
//            .placeholder(R.mipmap.img_loading)
            .thumbnail(Glide.with(context).load(R.mipmap.img_loading).centerCrop())
            .error(R.mipmap.img_fail)
            .into(this)
}


val AppRestoredBean.savedPrice: Int?
    get() {
        val initPrice = steamApp.initPrice
        val purchased = steamApp.purchasedPrice
        initPrice?.let {
            if (purchased != null) {
                return it - purchased
            }
        }
        return null
    }

val AppRestoredBean.savedPercent: Int?
    get() {
        savedPrice?.let {
            if (steamApp.initPrice != null) {
                val percentFloat = it * 100f / steamApp.initPrice!!
                return percentFloat.toInt()
            }
        }
        return null
    }