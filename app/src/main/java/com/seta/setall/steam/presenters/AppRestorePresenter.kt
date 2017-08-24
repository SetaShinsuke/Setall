package com.seta.setall.steam.presenters

import com.seta.setall.common.mvp.BasePresenter
import com.seta.setall.steam.api.models.AppRestoredBean
import com.seta.setall.steam.db.SteamDb
import com.seta.setall.steam.mvpViews.AppRestoreMvpView

/**
 * Created by SETA_WORK on 2017/8/23.
 */
class AppRestorePresenter : BasePresenter<AppRestoreMvpView>() {

    fun loadApps(typeList: List<String> = ArrayList()) {
        //TODO:筛选类型
        val types = typeList.filter { it is String }
                .toTypedArray()
        SteamDb.instance.findAllApps(*types) {
            appsDb ->
            mvpView?.onAppsRestored(
                    appsDb.map { AppRestoredBean(it, null, null) })
        }
    }
}