package com.seta.setall.steam.presenters

import com.seta.setall.common.extensions.logD
import com.seta.setall.common.http.Network
import com.seta.setall.common.mvp.BasePresenter
import com.seta.setall.steam.api.models.SteamLoginBean
import com.seta.setall.steam.mvpViews.SteamLoginView
import com.seta.setall.steam.utils.SteamException
import rx.Subscriber

/**
 * Created by SETA_WORK on 2017/7/4.
 */
class SteamLoginPresenter : BasePresenter<SteamLoginView>() {

    fun loginWithUrlName(urlName: String) {
        Network.steamUserApi.getIdByVanityUrl(urlName)
                .map { it.response }
                .doSubscribe(object : Subscriber<SteamLoginBean>() {
                    override fun onNext(t: SteamLoginBean) {
                        logD("onNext : $t")
                        if (t.success != 1) {
                            mvpView?.onLoginFail(SteamException(t.message))
                            return
                        }
                        mvpView?.onLoginSuccess(t)
                    }

                    override fun onCompleted() {
                        logD("onComplete")
                    }

                    override fun onError(e: Throwable) {
                        logD("onError : $e")
                        mvpView?.onLoginFail(e)
                    }
                }
                )
    }
}