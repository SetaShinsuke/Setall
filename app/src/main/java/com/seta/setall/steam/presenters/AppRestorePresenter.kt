package com.seta.setall.steam.presenters

import com.seta.setall.common.mvp.BasePresenter
import com.seta.setall.steam.api.models.AppRestoredBean
import com.seta.setall.steam.db.SteamDb
import com.seta.setall.steam.mvpViews.AppRestoreMvpView

/**
 * Created by SETA_WORK on 2017/8/23.
 */
class AppRestorePresenter : BasePresenter<AppRestoreMvpView>() {

    fun restoreApps(type: String? = null) {
        //TODO:筛选类型
        SteamDb.instance.findAllApps {
            appsDb ->
            mvpView?.onAppsRestored(
                    appsDb.map { AppRestoredBean(it, null, null) })
        }
    }
}