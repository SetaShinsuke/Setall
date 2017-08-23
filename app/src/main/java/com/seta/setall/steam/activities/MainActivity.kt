package com.seta.setall.steam.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.LinearLayout
import com.seta.setall.R
import com.seta.setall.common.extensions.logD
import com.seta.setall.common.framework.BaseActivity
import com.seta.setall.common.views.adapters.BasicAdapter
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.db.SteamDb
import com.seta.setall.steam.domain.models.SteamApp
import com.seta.setall.steam.extensions.DelegateSteam
import com.seta.setall.steam.extensions.loadImg
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_restored_app.view.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import kotlin.properties.Delegates

class MainActivity : BaseActivity() {
    var userId: String? by DelegateSteam.steamPreference(this, SteamConstants.STEAM_USER_ID, "")
    //    val ownedGamePresenter: OwnedGamesPresenter = OwnedGamesPresenter()
    var adapter by Delegates.notNull<BasicAdapter<SteamApp>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (userId == "") {
            logD("userId : $userId")
            startActivity<SteamLoginActivity>()
            finish()
        }
        mTvMsg.text = "User id : $userId\nTransActions : loading..."
        mRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        adapter = BasicAdapter<SteamApp>(R.layout.item_restored_app) {
            itemView, positin, steamApp ->
            with(itemView) {
                mIvLogo.loadImg(steamApp.logoImgUrl)
            }
        }
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
                SteamDb.instance.export(this@MainActivity)?.let {
                    toast("导出成功!\n$it")
                    return
                }
                toast("导出失败!")
            }
        }
    }
}
