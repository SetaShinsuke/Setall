package com.seta.setall.steam.mvpViews

import com.seta.setall.common.mvp.MvpView
import com.seta.setall.steam.api.models.GameDetailBean
import com.seta.setall.steam.api.models.OwnedGameBean
import com.seta.setall.steam.api.models.PlayerInfoBean
import com.seta.setall.steam.api.models.SteamLoginBean

/**
 * Created by SETA_WORK on 2017/7/4.
 */
interface SteamLoginView : MvpView {
    fun onLoginSuccess(steamLoginBean: SteamLoginBean)
    fun onLoginFail(t: Throwable)
}

interface OwnedGamesView : MvpView {
    fun onGamesLoad(ownedGameBean: OwnedGameBean)
    fun onGamesLoadFail(t: Throwable)
}

interface PlayerInfoMvpView : MvpView {
    fun onPlayerInfoLoad(playerInfoBean: PlayerInfoBean)
    fun onPlayerInfoLoadFail(t: Throwable)
}

interface GameDetailMvpView : MvpView {
    fun onGameDetailLoad(gameDetails: List<GameDetailBean>)
    fun onGameDetailLoadFail(t: Throwable)
}