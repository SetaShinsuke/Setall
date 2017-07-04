package com.seta.setall.steam

import com.seta.setall.common.mvp.MvpView
import com.seta.setall.steam.models.SteamLoginBean

/**
 * Created by SETA_WORK on 2017/7/4.
 */
interface SteamLoginView : MvpView {
    fun onLoginSuccess(steamLoginBean: SteamLoginBean)
    fun onLoginFail(t: Throwable)
}