package com.seta.setall.steam.presenters

import com.seta.setall.common.extensions.logD
import com.seta.setall.common.extensions.logW
import com.seta.setall.common.http.Network
import com.seta.setall.common.mvp.BasePresenter
import com.seta.setall.steam.api.models.PackageDetailBean
import com.seta.setall.steam.mvpViews.PackageDetailMvpView
import com.seta.setall.steam.utils.SteamUtilMethods
import rx.Observable
import rx.Subscriber

/**
 * Created by SETA_WORK on 2017/8/3.
 */
class PackageDetailPresenter : BasePresenter<PackageDetailMvpView>() {

    fun loadPackages(packageIds: List<*>) {
        val ids = packageIds.filterIsInstance<Int>()
        val reqArray = ids.map { Network.steamGameApi.getBundlePackDetail(it) }
        val observable: Observable<List<PackageDetailBean>> = Observable.zip(reqArray) {
            val packDetails = ArrayList<PackageDetailBean>()
            it.forEach {
                packDetails.add(SteamUtilMethods.createPackageDetailBean(it))
            }
            return@zip packDetails
        }
        observable.doSubscribe(object : Subscriber<List<PackageDetailBean>>() {
            override fun onCompleted() {
                logD("loadPackages onCompleted")
            }

            override fun onError(e: Throwable?) {
                logW("loadPackages on error : ${e?.message}")
                mvpView?.onPackageDetailLoadFail(e)
            }

            override fun onNext(t: List<PackageDetailBean>) {
                logD("loadPackages onNext : $t")
                mvpView?.onPackageDetailLoad(t)
            }

        })
    }
}