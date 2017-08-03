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
import com.seta.setall.steam.extensions.loadImg
import com.seta.setall.steam.mvpViews.GameDetailMvpView
import com.seta.setall.steam.presenters.GameDetailPresenter
import kotlinx.android.synthetic.main.activity_game_list.*
import kotlinx.android.synthetic.main.item_game_detail.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import kotlin.properties.Delegates

class GameListActivity : BaseActivity(), GameDetailMvpView {

    val gameDetailPresenter: GameDetailPresenter = GameDetailPresenter()
    var adapter by Delegates.notNull<BasicAdapter<GameDetailBean>>()
    val selectedIds = ArrayList<Int>()

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
                mIvHeaderCheck.setVisible(selectedIds.contains(data.steam_appid))
                mTvPriceFinal.money = data.price_overview?.final
                mTvPriceInit.money = data.price_overview?.initial
                mTvPriceInit.deleteLine()
                mTvPriceInit.setVisible(data.price_overview?.final != data.price_overview?.initial)
                onClick {
                    if (selectedIds.contains(data.steam_appid)) {
                        selectedIds.remove(data.steam_appid)
                    } else {
                        selectedIds.add(data.steam_appid)
                    }
                    adapter.notifyItemChanged(data)
                }
            }
        }
        mRvGames.adapter = adapter

        val fullGameIds: List<Int> = intent.getIntegerArrayListExtra(SteamConstants.GAME_IDS)
        logD("Full game ids : $fullGameIds")
        loadingDialog?.show()
        gameDetailPresenter.loadGameDetails(fullGameIds)
    }

    override fun onGameDetailLoad(gameDetails: List<GameDetailBean>) {
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
                if (selectedIds.isEmpty()) {
                    toast(R.string.no_item_selected)
                    return super.onOptionsItemSelected(item)
                }
                startActivity<CreateTransActivity>()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (selectedIds.isNotEmpty()) {
            alert {
                titleResource = R.string.hint
                message = String.format(getString(R.string.cancel_game_select_hint), selectedIds.size)
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
