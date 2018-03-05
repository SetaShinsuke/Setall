package com.seta.setall.steam.mvpViews

import com.seta.setall.common.mvp.MvpView
import com.seta.setall.steam.api.models.*
import com.seta.setall.steam.domain.models.SteamApp
import com.seta.setall.steam.domain.models.Transaction

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

interface GameDlcPackMvpView : MvpView {
    fun onGameLoad(gameDlcPackBean: GameDlcPackBean)
    fun onGamesLoad(games: List<GameDlcPackBean>)
    fun onGameLoadFail(t: Throwable)
}

interface PackageDetailMvpView : MvpView {
    fun onPackageDetailLoad(packDetails: List<PackageDetailBean>)
    fun onPackageDetailLoadFail(t: Throwable?)
}

interface SteamAppDetailMvpView : MvpView {
    fun onAppsLoad(apps: ArrayList<SteamApp>)
    fun onAppsLoadFail(t: Throwable)
}

interface AppRestoreMvpView : MvpView {
    fun onAppsRestored(apps: List<AppRestoredBean>)
    fun onAppRestoreFail(t: Throwable)
}

interface TransactionRestoreMvpView : MvpView {
    fun onTranListRestored(tranList: List<Transaction>)
    fun onTranListRestoreFail(t: Throwable)
}

interface SteamAppEditMvpView : MvpView {
    fun onAppRemoved()
    fun onAppRemoveFail(t: Throwable)
}

interface DataBackupMvpView : MvpView {
    fun onDataBackup(message: String)
    fun onDataBackupFail(t: Throwable)
}