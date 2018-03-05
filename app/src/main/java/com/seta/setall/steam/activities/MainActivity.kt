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
import com.seta.setall.steam.api.SteamConstants.Companion.TYPE_BUNDLE_PACK
import com.seta.setall.steam.api.models.AppRestoredBean
import com.seta.setall.steam.db.SteamDb
import com.seta.setall.steam.events.TransEditEvent
import com.seta.setall.steam.extensions.*
import com.seta.setall.steam.mvpViews.AppRestoreMvpView
import com.seta.setall.steam.mvpViews.SteamAppEditMvpView
import com.seta.setall.steam.presenters.AppRestorePresenter
import com.seta.setall.steam.presenters.SteamAppEditPresenter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_restored_app.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.alert
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.sdk25.coroutines.onLongClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import kotlin.properties.Delegates

class MainActivity : BaseActivity(), AppRestoreMvpView, SteamAppEditMvpView {
    var userId: String? by DelegateSteam.steamPreference(this, SteamConstants.STEAM_USER_ID, "")
    //    val ownedGamePresenter: OwnedGamesPresenter = OwnedGamesPresenter()
    var adapter by Delegates.notNull<BasicAdapter<AppRestoredBean>>()
    val appRestorePresenter = AppRestorePresenter()
    val mAppEditPresenter = SteamAppEditPresenter()
    val showTypes: ArrayList<String> = arrayListOf(SteamConstants.TYPE_UNKNOWN, SteamConstants.TYPE_GAME)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setHomeAsBackEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        setSwipeBackEnable(false)
        registerBus()
        if (userId == "") {
            logD("userId : $userId")
            startActivity<SteamLoginActivity>()
            finish()
            return
        }
        mRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        adapter = BasicAdapter<AppRestoredBean>(R.layout.item_restored_app) { itemView, position, app ->
            with(itemView) {
                mIvLogo.setVisible(true)
                mIvLogo.loadImg(app.steamApp.logoImgUrl)
                mAppName.text = app.steamApp.name
                mTvPackBadge.setVisible(true)
                mTvPriceInit.deleteLine().money = app.steamApp.initPrice
                mTvPurchasedPrice.money = app.steamApp.purchasedPrice
                val showSave: Boolean? = app.savedPrice?.let {
                    return@let it != 0
                }
                mTvPriceSaved.setVisible(showSave)
                mTvPriceSaved.isSelected = app.isAtAdvantage()
                mTvPurchasedPrice.isSelected = app.isAtAdvantage()
                mTvPriceSaved.text = "-￥${app.savedPrice?.toYuanInt()}(${app.savedPercent}%)"

                //类型标识
                mTvPackBadge.setVisible(false)
//                mTvPriceInit.setVisible(app.currentSaved != null && app.currentSaved != 0)
                mTvCurrentDiscount.setVisible(app.currentSaved != null && app.currentSaved != 0)
                mTvCurrentDiscount.text = "-￥${app.currentSaved?.toYuanInt()}(${app.currentSavedPercent}%)"
                mTvPackDetail.setVisible(false)
                mTvPurchasedDate.text = "购买日期: ${app.steamApp.purchasedDate.toYMD()}"
                when (app.steamApp.type) {
                    SteamConstants.TYPE_BUNDLE_PACK -> {
                        mTvPackBadge.backgroundResource = R.color.steam_theme_color_accent
                        mTvPackBadge.text = "Pack"
                        mTvPackBadge.setVisible(true)
                        mIvLogo.setVisible(false)
                        mTvPriceCurrent.money = app.packageDetailBean?.price?.final
                        logD("Type pack, games: ${app.steamApp.games}")
                        mTvPackDetail.setVisible(app.steamApp.games?.isNotEmpty())
                        var detailStr = "共包含${app.steamApp.games?.size}件物品"
                        app.steamApp.games?.forEachIndexed { index, steamApp ->
                            detailStr += steamApp.name
                            if (index < app.steamApp.games.lastIndex) {
                                detailStr += "\n"
                            }
                        }
                        mTvPackDetail.text = detailStr
                    }
                    SteamConstants.TYPE_DLC -> {
                        mTvPackBadge.backgroundResource = R.color.steam_theme_color
                        mTvPackBadge.text = "DLC"
                        mTvPackBadge.setVisible(true)
                        mTvPriceCurrent.money = app.gameDetailBean?.price_overview?.final
                    }
                    SteamConstants.TYPE_GAME -> {
                        mTvPriceCurrent.money = app.gameDetailBean?.price_overview?.final
                    }
                }
                onLongClick {
                    alert {
                        title = "确认删除?"
                        positiveButton(R.string.confirm) {
                            loadingDialog?.show()
                            mAppEditPresenter.removeSteamApp(app.steamApp.appId, app.steamApp.type == TYPE_BUNDLE_PACK)
                        }
                        negativeButton(R.string.cancel) {

                        }
                        show()
                    }
                }
            }
        }
        mRecyclerView.adapter = adapter
        appRestorePresenter.attachView(this)
        loadingDialog
        loadingDialog?.show()
        val returnValue = appRestorePresenter.loadApps(showTypes)
        logD("Return value : $returnValue")
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: TransEditEvent) {
        appRestorePresenter.loadApps(showTypes)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.order_time -> {
                val data = adapter.data.sortedBy { it.steamApp.purchasedDate }
                adapter.refreshData(data)
            }
            R.id.order_name -> {
                val data = adapter.data.sortedBy { it.steamApp.name.toUpperCase() }
                adapter.refreshData(data)
            }
            R.id.order_price -> {
                val data = adapter.data.sortedBy { it.steamApp.purchasedPrice }
                adapter.refreshData(data)
            }
            R.id.order_saved -> {
                val data = adapter.data.sortedByDescending { it.savedPercent }
                adapter.refreshData(data)
            }
            R.id.menu_add_trans -> startActivity<OwnedGamesActivity>()
            R.id.menu_check_trans -> startActivity<TransactionListActivity>()
            R.id.menu_export_db -> {
                SteamDb.instance.export(this@MainActivity)?.let {
                    toast("导出成功!\n$it")
                    return super.onOptionsItemSelected(item)
                }
                toast("导出失败!")
            }
            R.id.menu_backup -> SteamDb.instance.backUp {
                toast(it)
            }
            R.id.menu_logout -> {
                alert {
                    titleResource = R.string.title_logout
                    positiveButton(R.string.confirm) {
                        userId = null
                        finish()
                        toast("已注销！")
                        startActivity<SteamLoginActivity>()
                    }
                    negativeButton(R.string.cancel) {
                        it.dismiss()
                    }
                    show()
                }
            }
        //筛选
            R.id.filter_dlc -> {
                loadingDialog?.show()
                showTypes.switch(SteamConstants.TYPE_DLC)
                appRestorePresenter.loadApps(showTypes)
            }
            R.id.filter_pack -> {
                loadingDialog?.show()
                showTypes.switch(SteamConstants.TYPE_BUNDLE_PACK)
                appRestorePresenter.loadApps(showTypes)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        appRestorePresenter.detachView()
        mAppEditPresenter.detachView()
    }

    override fun onAppsRestored(apps: List<AppRestoredBean>) {
        logD("App restored : $apps")
        loadingDialog?.dismiss()
        adapter.refreshData(apps)
    }

    override fun onAppRestoreFail(t: Throwable) {
        loadingDialog?.dismiss()
        toast("Restore apps fail !\n${t.message}")
    }

    override fun onAppRemoved() {
        loadingDialog?.dismiss()
        toast("已删除!")
    }

    override fun onAppRemoveFail(t: Throwable) {
        loadingDialog?.dismiss()
        toast("删除失败!\n${t.message}")
    }
}
