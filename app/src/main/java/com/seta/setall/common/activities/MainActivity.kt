package com.seta.setall.common.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.seta.setall.R
import com.seta.setall.steam.SteamLoginView
import com.seta.setall.steam.models.SteamLoginBean
import com.seta.setall.steam.presenters.SteamLoginPresenter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SteamLoginView {

    val mSteamLoginPresenter: SteamLoginPresenter = SteamLoginPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSteamLoginPresenter.attachView(this)
        mSteamLoginPresenter.loginWithUrlName("sweater5656v")
    }

    override fun onDestroy() {
        super.onDestroy()
        mSteamLoginPresenter.detachView()
    }

    override fun onLoginSuccess(steamLoginBean: SteamLoginBean) {
        textView.text = "SteamId : \n${steamLoginBean.steamid}"
    }

    override fun onLoginFail(t: Throwable) {
        textView.text = "Get steam id fail : $t"
    }
}
