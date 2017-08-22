package com.seta.setall.steam.activities

import android.os.Bundle
import android.view.View
import com.seta.setall.R
import com.seta.setall.common.extensions.logD
import com.seta.setall.common.framework.BaseActivity
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.db.SteamDb
import com.seta.setall.steam.extensions.DelegateSteam
import kotlinx.android.synthetic.main.activity_steam_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class SteamMainActivity : BaseActivity() {
    var userId: String? by DelegateSteam.steamPreference(this, SteamConstants.STEAM_USER_ID, "")
//    val ownedGamePresenter: OwnedGamesPresenter = OwnedGamesPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steam_main)
        if (userId == "") {
            logD("userId : $userId")
            startActivity<SteamLoginActivity>()
            finish()
        }
        mTvMsg.text = "User id : $userId\nTransActions : loading..."
//        enableHomeAsBack(true)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.mBtnLogout -> {
                userId = null
                finish()
                toast("已注销！")
                startActivity<SteamLoginActivity>()
            }
            R.id.mBtnAddTrans -> {
                startActivity<OwnedGamesActivity>()
            }
            R.id.mBtnExpDb -> {
                SteamDb.instance.export(this@SteamMainActivity)?.let {
                    toast("导出成功!\n$it")
                    return
                }
                toast("导出失败!")
            }
        }
    }
}
