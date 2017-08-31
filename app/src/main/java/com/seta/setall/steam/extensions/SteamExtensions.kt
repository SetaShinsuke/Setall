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
            if (steamApp.initPrice != null && steamApp.initPrice != 0) {
                val percentFloat = it * 100f / steamApp.initPrice!!
                return percentFloat.toInt()
            }
        }
        return null
    }

val AppRestoredBean.currentSaved: Int?
    get() {
        when (steamApp.type) {
            SteamConstants.TYPE_GAME, SteamConstants.TYPE_DLC -> {
                return gameDetailBean?.price_overview?.final?.let {
                    return@let steamApp.initPrice?.minus(it)
                }
            }
            SteamConstants.TYPE_BUNDLE_PACK -> {
                return packageDetailBean?.price?.final?.let {
                    return@let steamApp.initPrice?.minus(it)
                }
            }
        }
        return null
    }

val AppRestoredBean.currentSavedPercent: Int?
    get() {
        currentSaved?.let {
            currentSaved ->
            return if (steamApp.initPrice != 0) {
                (currentSaved * 100f / steamApp.initPrice!!).toInt()
            } else {
                return null
            }
        }
        return null
    }