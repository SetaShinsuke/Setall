package com.seta.setall.steam.extensions

import android.content.Context
import com.azusasoft.kotlintest.extensions.Preference
import com.seta.setall.steam.api.SteamConstants

/**
 * Created by SETA_WORK on 2017/7/5.
 */
object DelegateSteam {
    fun <T> steamPreference(context: Context, name: String, default: T) = Preference(SteamConstants.STEAM_PRF_NAME, context, name, default)
//    fun removePreference(context: Context, name: String) {
//
//    }
}