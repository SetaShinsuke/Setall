package com.seta.setall.steam.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import com.seta.setall.R
import com.seta.setall.common.extensions.deleteLine
import com.seta.setall.common.extensions.logD
import com.seta.setall.common.extensions.money
import com.seta.setall.common.extensions.setVisible
import com.seta.setall.common.framework.BaseActivity
import com.seta.setall.common.views.adapters.BasicAdapter
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.api.models.GameDetailBean
import com.seta.setall.steam.domain.TransManager
import com.seta.setall.steam.domain.models.SteamApp
import com.seta.setall.steam.events.CreateStartEvent
import com.seta.setall.steam.extensions.loadImg
import com.seta.setall.steam.mvpViews.GameDetailMvpView
import com.seta.setall.steam.presenters.GameDetailPresenter
import kotlinx.android.synthetic.main.activity_game_list.*
import kotlinx.android.synthetic.main.item_game_detail.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.alert
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import kotlin.properties.Delegates

class GameListActivity : BaseActivity(), GameDetailMvpView {

    val gameDetailPresenter: GameDetailPresenter = GameDetailPresenter()
    var adapter by Delegates.notNull<BasicAdapter<GameDetailBean>>()
    //    val selectedIds = ArrayList<Int>()
    val selectedApps = ArrayList<GameDetailBean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_list)
        gameDetailPresenter.attachView(this)
        mRvGames.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        adapter = BasicAdapter(R.layout.item_game_detail) {
            view, position, data ->
            with(view) {
                dlcBadge.setVisible(data.type == "dlc")
                mTvGameName.text = data.name
                mIvHeader.loadImg(data.header_image)
                mIvHeaderCheck.setVisible(selectedApps.contains(data))
                mTvPriceFinal.money = data.price_overview?.final
                mEtPriceInit.money = data.price_overview?.initial
                mEtPriceInit.deleteLine()
                mEtPriceInit.setVisible(data.price_overview?.final != data.price_overview?.initial)
                onClick {
                    if (selectedApps.contains(data)) {
                        selectedApps.remove(data)
                    } else {
                        selectedApps.add(data)
                    }
                    adapter.notifyItemChanged(data)
                }
            }
        }
        mRvGames.adapter = adapter

        val fullGameIds: List<Int> = intent.getIntegerArrayListExtra(SteamConstants.GAME_IDS)
        logD("Full game ids : $fullGameIds")
        if (fullGameIds.isNotEmpty()) {
            loadingDialog?.show()
            gameDetailPresenter.loadGameDetails(fullGameIds)
        } else {
            startActivity<CreateTransActivity>()
        }
        registerBus()
    }

    override fun onGameDetailLoad(gameDetails: List<GameDetailBean>) {
        if (selectedApps.isEmpty()) {
            selectedApps.addAll(gameDetails.filter { it.type == "game" })
        }
        adapter.refreshData(gameDetails)
        loadingDialog?.hide()
    }

    override fun onGameDetailLoadFail(t: Throwable) {
        toast(R.string.games_load_fail)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.go_next, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        when (id) {
            R.id.menu_commit -> {
                if (TransManager.steamApps.isEmpty()) {
                    toast(R.string.no_item_selected)
                    return super.onOptionsItemSelected(item)
                }
                val gameSimpleBeans = TransManager.steamApps.filter { selectedApps.map { it.steam_appid }.contains(it.appId) }
                TransManager.steamApps.removeAll(gameSimpleBeans)
                val toAddApps = ArrayList<SteamApp>()
//                gameSimpleBeans.forEach {
//                    gameSimpleBean ->
//                    val id: Int = gameSimpleBean.appId
//                    val gameDetailBean = selectedApps.find { it.steam_appid == id }
//                    gameDetailBean?.let { toAddApps.add(SteamApp(it, gameSimpleBean.iconImgUrl, gameSimpleBean.logoImgUrl)) }
//                }
                selectedApps.forEach {
                    detailBean ->
                    val id: Int = detailBean.steam_appid
                    val gameSimpleBean = gameSimpleBeans.find { it.appId == id }
                    toAddApps.add(SteamApp(detailBean, gameSimpleBean?.logoImgUrl))
                }
                TransManager.steamApps.addAll(toAddApps.distinct())
                startActivity<CreateTransActivity>()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: CreateStartEvent) {
        finish()
    }

    override fun onBackPressed() {
        if (selectedApps.isNotEmpty()) {
            alert {
                titleResource = R.string.hint
                message = String.format(getString(R.string.cancel_game_select_hint), selectedApps.size)
                positiveButton(R.string.confirm) { dialog ->
                    dialog.dismiss()
                    super.onBackPressed()
                }
                negativeButton(R.string.cancel) { dialog -> dialog.dismiss() }
                show()
            }
            return
        }
        super.onBackPressed()
    }
}
