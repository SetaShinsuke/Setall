package com.seta.setall.common.activities

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.seta.setall.R
import com.seta.setall.common.extensions.setMessage
import com.seta.setall.common.extensions.toast
import com.seta.setall.steam.api.models.SteamLoginBean
import com.seta.setall.steam.mvpViews.SteamLoginView
import com.seta.setall.steam.presenters.SteamLoginPresenter
import kotlinx.android.synthetic.main.activity_steam_login.*
import org.jetbrains.anko.toast
import kotlin.properties.Delegates

class SteamLoginActivity : AppCompatActivity(), SteamLoginView {

    val mSteamLoginPresenter: SteamLoginPresenter = SteamLoginPresenter()
    var loadingDialog by Delegates.notNull<ProgressDialog>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steam_login)

        loadingDialog = ProgressDialog(this)
        mSteamLoginPresenter.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSteamLoginPresenter.detachView()
        loadingDialog.dismiss()
    }

    override fun onLoginSuccess(steamLoginBean: SteamLoginBean) {
        toast(R.string.login_success)
        loadingDialog.dismiss()
        finish()
    }

    override fun onLoginFail(t: Throwable) {
        loadingDialog.dismiss()
        toast(R.string.login_fail, " " + t.message)
    }

    fun onClick(view: View) {
        mSteamLoginPresenter.loginWithUrlName(mEtVanityUrlName.text.toString())
        with(loadingDialog) {
            setMessage(R.string.login_loading)
            show()
        }
    }
}
