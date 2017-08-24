package com.seta.setall.steam.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import com.seta.setall.R
import com.seta.setall.common.extensions.*
import com.seta.setall.common.framework.BaseActivity
import com.seta.setall.common.views.adapters.BasicAdapter
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.api.models.AppRestoredBean
import com.seta.setall.steam.db.SteamDb
import com.seta.setall.steam.extensions.DelegateSteam
import com.seta.setall.steam.extensions.loadImg
import com.seta.setall.steam.extensions.savedPercent
import com.seta.setall.steam.extensions.savedPrice
import com.seta.setall.steam.mvpViews.AppRestoreMvpView
import com.seta.setall.steam.presenters.AppRestorePresenter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_restored_app.view.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import kotlin.properties.Delegates

class MainActivity : BaseActivity(), AppRestoreMvpView {

    var userId: String? by DelegateSteam.steamPreference(this, SteamConstants.STEAM_USER_ID, "")
    //    val ownedGamePresenter: OwnedGamesPresenter = OwnedGamesPresenter()
    var adapter by Delegates.notNull<BasicAdapter<AppRestoredBean>>()
    val appRestorePresenter = AppRestorePresenter();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (userId == "") {
            logD("userId : $userId")
            startActivity<SteamLoginActivity>()
            finish()
        }
        mRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        adapter = BasicAdapter<AppRestoredBean>(R.layout.item_restored_app) {
            itemView, positin, app ->
            with(itemView) {
                mIvLogo.setVisible(true)
                mIvLogo.loadImg(app.steamApp.logoImgUrl)
                mAppName.text = app.steamApp.name
                mTvPackBadge.setVisible(true)
                mTvPriceInit.deleteLine().money = app.steamApp.initPrice
                mTvPurchasedPrice.money = app.steamApp.purchasedPrice
                mTvPriceSaved.setVisible(true)
                mTvPriceSaved.text = "-￥${app.savedPrice?.toYuanInt()}(${app.savedPercent}%)"

                //类型标识
                when (app.steamApp.type) {
                    SteamConstants.TYPE_BUNDLE_PACK -> {
                        mTvPackBadge.backgroundResource = R.color.steam_theme_color_accent
                        mTvPackBadge.text = "Pack"
                        mIvLogo.setVisible(false)
                    }
                    SteamConstants.TYPE_DLC -> {
                        mTvPackBadge.backgroundResource = R.color.steam_theme_color
                        mTvPackBadge.text = "DLC"
                    }
                    else -> mTvPackBadge.setVisible(false)
                }

            }
        }
        mRecyclerView.adapter = adapter
        appRestorePresenter.attachView(this)
        appRestorePresenter.restoreApps()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_add_trans -> startActivity<OwnedGamesActivity>()
            R.id.menu_export_db -> {
                SteamDb.instance.export(this@MainActivity)?.let {
                    toast("导出成功!\n$it")
                    return super.onOptionsItemSelected(item)
                }
                toast("导出失败!")
            }
            R.id.menu_logout -> {
                userId = null
                finish()
                toast("已注销！")
                startActivity<SteamLoginActivity>()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAppsRestored(apps: List<AppRestoredBean>) {
        logD("App restored : $apps")
        adapter.refreshData(apps)
    }

    override fun onAppRestoreFail(t: Throwable) {
        toast("Restore apps fail !\n${t.message}")
    }
}
