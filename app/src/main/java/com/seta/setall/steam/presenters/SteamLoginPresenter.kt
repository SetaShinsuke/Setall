package com.seta.setall.steam.presenters

import com.seta.setall.common.extensions.logD
import com.seta.setall.common.http.Network
import com.seta.setall.common.mvp.BasePresenter
import com.seta.setall.steam.SteamLoginView
import com.seta.setall.steam.api.SteamServer
import com.seta.setall.steam.models.SteamLoginBean
import com.seta.setall.steam.utils.SteamException
import rx.Subscriber

/**
 * Created by SETA_WORK on 2017/7/4.
 */
class SteamLoginPresenter : BasePresenter<SteamLoginView>() {

    fun loginWithUrlName(urlName: String) {
        Network.steamUserApi.getIdByVanityUrl(SteamServer.STEAM_API_KEY, urlName)
                .map { it.response }
                .doSubscribe(object : Subscriber<SteamLoginBean>() {
                    override fun onNext(t: SteamLoginBean) {
                        logD("onNext : $t")
                        mvpView?.onLoginSuccess(t) ?: onError(SteamException(t.message))
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