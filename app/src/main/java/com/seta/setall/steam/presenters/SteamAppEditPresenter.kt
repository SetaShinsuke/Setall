package com.seta.setall.steam.presenters

import com.seta.setall.common.mvp.BasePresenter
import com.seta.setall.steam.db.SteamDb
import com.seta.setall.steam.mvpViews.SteamAppEditMvpView
import com.seta.setall.steam.utils.SteamException

/**
 * Created by SETA_WORK on 2017/12/28.
 */
class SteamAppEditPresenter : BasePresenter<SteamAppEditMvpView>() {

    fun removeSteamApp(appId: Int, isPack: Boolean) {
        val result = SteamDb.instance.removeApp(appId, isPack)
        if (result == null || result <= 0) {
            mvpView?.onAppRemoveFail(SteamException("删除SteamApp失败, id: $appId, result: $result"))
        } else {
            mvpView?.onAppRemoved()
        }
    }
}