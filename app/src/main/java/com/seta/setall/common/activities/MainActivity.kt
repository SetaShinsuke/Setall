package com.seta.setall.common.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.seta.setall.R
import com.seta.setall.common.http.Network
import com.seta.setall.steam.api.SteamServer.Companion.STEAM_API_KEY
import org.jetbrains.anko.toast
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val steamApi = Network.steamUserApi
        steamApi.getIdByVanityUrl(STEAM_API_KEY, "sweater5656v")
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.computation())
                .subscribe(object : Subscriber<Any>() {
                    override fun onCompleted() {
//                        log("onComplete")
                    }

                    override fun onNext(t: Any?) {
//                        log("onNext : $t")
                        toast("onNext : $t")
                    }

                    override fun onError(e: Throwable?) {
//                        log("onError : $e , detail : ${e?.message}")
                        toast("onError : ${e?.message}")
                    }

                })
    }
}
