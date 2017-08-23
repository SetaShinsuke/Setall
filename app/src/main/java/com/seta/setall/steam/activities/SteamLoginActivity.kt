package com.seta.setall.steam.activities

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import com.seta.setall.R
import com.seta.setall.common.extensions.logD
import com.seta.setall.common.extensions.setMessage
import com.seta.setall.common.extensions.toast
import com.seta.setall.common.framework.BaseActivity
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.api.models.SteamLoginBean
import com.seta.setall.steam.extensions.DelegateSteam
import com.seta.setall.steam.mvpViews.SteamLoginView
import com.seta.setall.steam.presenters.SteamLoginPresenter
import kotlinx.android.synthetic.main.activity_steam_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class SteamLoginActivity : BaseActivity(), SteamLoginView {

    var steamUserId: String by DelegateSteam.steamPreference(this, SteamConstants.STEAM_USER_ID, "")
//    var loadingDialog by Delegates.notNull<ProgressDialog>()

    val mSteamLoginPresenter: SteamLoginPresenter = SteamLoginPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steam_login)

        loadingDialog = ProgressDialog(this)
        mEtVanityUrlName.setText(steamUserId)
        mSteamLoginPresenter.attachView(this)
//        enableHomeAsBack(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSteamLoginPresenter.detachView()
        loadingDialog?.dismiss()
    }

    override fun onLoginSuccess(steamLoginBean: SteamLoginBean) {
        toast(R.string.login_success)
        loadingDialog?.dismiss()
        steamUserId = steamLoginBean.steamid
        finish()
        logD("Login success , id : $steamUserId")
        startActivity<MainActivity>()
    }

    override fun onLoginFail(t: Throwable) {
        loadingDialog?.dismiss()
        toast(R.string.login_fail, " " + t.message)
    }

    fun onClick(view: View) {
        mSteamLoginPresenter.loginWithUrlName(mEtVanityUrlName.text.toString())
        loadingDialog?.setMessage(R.string.login_loading)
        loadingDialog?.show()
    }
}
