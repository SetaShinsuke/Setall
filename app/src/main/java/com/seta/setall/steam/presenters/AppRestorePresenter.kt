package com.seta.setall.steam.presenters

import com.seta.setall.common.extensions.logD
import com.seta.setall.common.extensions.logW
import com.seta.setall.common.http.Network
import com.seta.setall.common.mvp.BasePresenter
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.api.models.AppRestoredBean
import com.seta.setall.steam.api.models.GameDetailBean
import com.seta.setall.steam.api.models.PackageDetailBean
import com.seta.setall.steam.db.SteamDb
import com.seta.setall.steam.mvpViews.AppRestoreMvpView
import com.seta.setall.steam.utils.SteamUtilMethods
import rx.Observable
import rx.Subscriber

/**
 * Created by SETA_WORK on 2017/8/23.
 */
class AppRestorePresenter : BasePresenter<AppRestoreMvpView>() {

    fun loadApps(typeList: List<String> = ArrayList()) {
        //TODO:筛选类型
        val types = typeList.filter { it is String }
                .toTypedArray()
        val res = SteamDb.instance.findAllApps(*types) {
            appsDb, observable ->
            //            mvpView?.onAppsRestored(
//                    appsDb.map { AppRestoredBean(it, null, null) })

            //拉取游戏详情
            val gamesAndDlcs = appsDb.filter { it.type == SteamConstants.TYPE_GAME || it.type == SteamConstants.TYPE_DLC }
            val appDetailObservable: Observable<List<GameDetailBean>>
                    = if (gamesAndDlcs.isNotEmpty()) {
                Observable.zip(gamesAndDlcs.map { Network.steamGameApi.getGameDetail(it.appId) }) {
                    val gameDetails = ArrayList<GameDetailBean>()
                    it.forEach {
                        SteamUtilMethods.createGameDetailBean(it)?.let {
                            gameDetails.add(it)
                        }
                    }
                    return@zip gameDetails
                }
            } else {
                Observable.just(ArrayList<GameDetailBean>())
            }
            //拉取包详情
            val packs = appsDb.filter { it.type == SteamConstants.TYPE_BUNDLE_PACK }
            val packDetailObservable: Observable<List<PackageDetailBean>>
                    = if (packs.isNotEmpty()) {
                Observable.zip(packs.map { Network.steamGameApi.getBundlePackDetail(it.appId) }) {
                    val packDetails = ArrayList<PackageDetailBean>()
                    it.forEach {
                        packDetails.add(SteamUtilMethods.createPackageDetailBean(it))
                    }
                    return@zip packDetails
                }
            } else {
                Observable.just(ArrayList<PackageDetailBean>())
            }

            Observable.zip(appDetailObservable, packDetailObservable) {
                gameDetails, packDetails ->
                val restoredApps: ArrayList<AppRestoredBean> = ArrayList()
                appsDb.forEach {
                    appDb ->
                    restoredApps.add(
                            AppRestoredBean(appDb,
                                    gameDetails.find { it.steam_appid == appDb.appId },
                                    packDetails.find { it.id == appDb.appId }
                            )
                    )
                }
                return@zip restoredApps
            }.doSubscribe(object : Subscriber<ArrayList<AppRestoredBean>>() {
                override fun onCompleted() {
                    logD("App restore onCompleted.")
                }

                override fun onNext(t: ArrayList<AppRestoredBean>) {
                    logD("App restore onNext : $t")
                    mvpView?.onAppsRestored(t)
                }

                override fun onError(e: Throwable) {
                    logW("Restore apps fail : $e")
                    mvpView?.onAppRestoreFail(e)
                }

            })
        }
        logD("res : $res")
        //整合 appDb,gameDetail,packDetail
    }
}